package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.repository.SubjectRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelParser {

    @Autowired
    private SampleRepository sampleRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudyRepository studyRepository;

    public ExcelParser(SampleRepository sampleRepository, SubjectRepository subjectRepository, StudyRepository studyRepository) {
        this.sampleRepository = sampleRepository;
        this.subjectRepository = subjectRepository;
        this.studyRepository = studyRepository;
    }

    public enum cellType {
        STRING, NUMERIC, DATE, BOOLEAN
    }

    public void readExcelFile(FileInputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();

        String studyName;
        List<Sample> sampleList = new ArrayList<>();
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

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

            //Creating Sample Objects
            Sample sample = new Sample();
            String coordinates = (String) getCellValue(currentRow.getCell(0), cellType.STRING);
            sample.setCoordinates(coordinates);


            double idDouble = (Double) getCellValue(currentRow.getCell(1), cellType.NUMERIC);
            int id = getNumericValue(idDouble);
            Subject sub = new Subject();
            sub.setId((long) id);

            sub.setStudyId(studyRepository.getIdFromName(studyName));
            subjectList.add(sub);
            sample.setSubject(sub);

            double visitDouble = (Double) getCellValue(currentRow.getCell(2), cellType.NUMERIC);
            int visit = getNumericValue(visitDouble);
            sample.setVisits(visit);

            Date date = (Date) getCellValue(currentRow.getCell(3), cellType.DATE);
            sample.setSampleDate(date);

            String amount = (String) getCellValue(currentRow.getCell(4), cellType.STRING);
            sample.setSample_amount(amount);

            String sampleType = (String) getCellValue(currentRow.getCell(5), cellType.STRING);
            sample.setSample_type(sampleType);

            String barcode = (String) getCellValue(currentRow.getCell(6), cellType.STRING);
            sample.setSample_barcode(barcode);

            sampleList.add(sample);
        }

        workbook.close();
        inputStream.close();

        Study study = new Study();
        study.setStudyDate(new Date());
        study.setStudyName(studyName);


        studyRepository.save(study);

        for (Subject subject : subjectList) {
            subjectRepository.save(subject);
        }

        for (Sample sample : sampleList) {
            sample.setStudy(study);
            sampleRepository.save(sample);
        }

        study.setListOfSamples(sampleList);
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

    private static int getNumericValue(double value) throws IOException {
        if (value == (int) value) {
            return (int) value;
        }
        throw new IOException("Expected int value, given Double");
    }
}
