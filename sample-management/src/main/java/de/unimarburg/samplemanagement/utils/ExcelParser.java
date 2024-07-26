package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.SampleDelivery;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.repository.SampleDeliveryRepository;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.repository.SubjectRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Component
public class ExcelParser {

    private SampleDeliveryRepository sampleDeliveryRepository;
    private SubjectRepository subjectRepository;
    private StudyRepository studyRepository;
    private ClientStateService clientStateService;

    @Autowired
    public ExcelParser(SampleDeliveryRepository sampleDeliveryRepository, SubjectRepository subjectRepository, StudyRepository studyRepository, ClientStateService clientStateService) {
        this.sampleDeliveryRepository = sampleDeliveryRepository;
        this.subjectRepository = subjectRepository;
        this.studyRepository = studyRepository;
        this.clientStateService = clientStateService;
    }

    public enum cellType {
        STRING, NUMERIC, DATE, BOOLEAN
    }

    public void readExcelFile(FileInputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();

        String studyName;
        List<Subject> subjectList = new ArrayList<>();

        // Reading first line
        if (iterator.hasNext()) {
            Row currentRow = iterator.next();
            studyName = (String) getCellValue(currentRow.getCell(1), cellType.STRING);
            if (iterator.hasNext()) {
                // Skip the header
                iterator.next();
            }
            else {
                throw new IOException("Only Study Name is available, no data");
            }
        }
        else {
            throw new IOException("Empty Excel File");
        }
        Study study = studyRepository.findByStudyName(studyName);
        // is this the currently selected study?
        Study selectedStudy = clientStateService.getClientState().getSelectedStudy();
        if (selectedStudy != null && !selectedStudy.getStudyName().equals(studyName)) {
            throw new IOException("Selected study does not match the study in the file");
        }
        SampleDelivery sampleDelivery = new SampleDelivery();
        sampleDelivery.setStudy(study);
        if (study == null) {
            throw new IOException("Study not found");
        }

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

            //Creating Sample Objects
            Sample sample = new Sample();
            String coordinates = (String) getCellValue(currentRow.getCell(0), cellType.STRING);
            sample.setCoordinates(coordinates);


            double aliasDouble = (Double) getCellValue(currentRow.getCell(1), cellType.NUMERIC);
            long alias = getNumericValue(aliasDouble);
            Subject subject = getSubject(alias, study);

            subjectList.add(subject);
            sample.setSubject(subject);

            double visitDouble = (Double) getCellValue(currentRow.getCell(2), cellType.NUMERIC);
            long visit = getNumericValue(visitDouble);
            sample.setVisits((int) visit);

            Date date = (Date) getCellValue(currentRow.getCell(3), cellType.DATE);
            sample.setSampleDate(date);

            String amount = (String) getCellValue(currentRow.getCell(4), cellType.STRING);
            sample.setSample_amount(amount);

            String sampleType = (String) getCellValue(currentRow.getCell(5), cellType.STRING);
            sample.setSample_type(sampleType);

            String barcode = (String) getCellValue(currentRow.getCell(6), cellType.STRING);
            sample.setSample_barcode(barcode);

            sample.setStudy(study);

            sampleDelivery.addSample(sample);
        }
        workbook.close();
        inputStream.close();

        sampleDeliveryRepository.save(sampleDelivery);

    }

    private Subject getSubject(long alias, Study study) {
        Optional<Subject> subject = subjectRepository.getSubjectByAliasAndStudy(alias, study);
        if (subject.isPresent()) {
            return subject.get();
        }
        Subject subjectNew = new Subject();
        subjectNew.setAlias(alias);
        subjectNew.setStudy(study);
        subjectNew = subjectRepository.save(subjectNew);
        return subjectNew;
    }

    public static Object getCellValue(Cell cell, cellType expectedType) throws IOException {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                if (expectedType == cellType.STRING) {
                    return cell.getStringCellValue();
                }
                break;
            case NUMERIC:
                if (expectedType == cellType.DATE && DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else if (expectedType == cellType.NUMERIC) {
                    return cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                if (expectedType == cellType.BOOLEAN) {
                    return cell.getBooleanCellValue();
                }
                break;
            case BLANK:
                return "";
            default:
                throw new IOException("Unexpected cell type: " + cell.getCellType());
        }
        // If cell type does not match expected type, throw an exception
        throw new IOException("Expected cell type: " + expectedType + ", but got: " + cell.getCellType());
    }

    private static long getNumericValue(double value) throws IOException {
        if (value == (long) value) {
            return (long) value;
        }
        throw new IOException("Expected int value, given Double");
    }
}
