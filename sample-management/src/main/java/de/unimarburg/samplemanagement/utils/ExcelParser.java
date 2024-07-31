package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.*;
import de.unimarburg.samplemanagement.repository.*;
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
    private AnalysisRepository analysisRepository;

    private AnalysisType selectedAnalysisType = null;
    @Autowired
    public ExcelParser(SampleDeliveryRepository sampleDeliveryRepository, SubjectRepository subjectRepository, StudyRepository studyRepository, ClientStateService clientStateService,AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
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
        sampleDelivery.setDeliveryDate(new Date());
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

            String barcode = "";
            try {
                barcode = (String) getCellValue(currentRow.getCell(6), cellType.STRING);
            } catch (IOException e) {
                Double barcodeDouble = (Double) getCellValue(currentRow.getCell(6), cellType.NUMERIC);
                barcode = String.valueOf(barcodeDouble).split("\\.")[0];
            }

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
        System.out.println("in getcell method");
        if (cell == null) {
            System.out.println("in null condition");
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                if (expectedType == cellType.STRING) {
                    System.out.println("in string condition");
                    return cell.getStringCellValue();
                }
                break;
            case NUMERIC:
                if (expectedType == cellType.DATE && DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else if (expectedType == cellType.NUMERIC) {
                    System.out.println("case numeric");
                    return cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                if (expectedType == cellType.BOOLEAN) {
                    System.out.println("case boolean");
                    return cell.getBooleanCellValue();
                }
                break;
            case BLANK:
                System.out.println("case Blank");
                return "";
            default:
                System.out.println("case exception");
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
    public void readArbeitslist(FileInputStream inputStream) throws IOException {
        System.out.println("I am in readfile");
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();

        String studyName;
        // Reading first line
        if (iterator.hasNext()) {
            System.out.println("I am in if part");
            Row studyNameRow = sheet.getRow(2);
            studyName = (String) getCellValue(studyNameRow.getCell(5), cellType.STRING);
            System.out.println("Study name is : " + studyName);
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
        System.out.println("selected study is: " + selectedStudy.getStudyName());
        if (selectedStudy != null && !selectedStudy.getStudyName().equals(studyName)) {
            throw new IOException("Selected study does not match the study in the file");
        }
        Row assay = sheet.getRow(3);
        String analysename = (String) getCellValue(assay.getCell(5),cellType.STRING );
        System.out.println("Analsis name is : " + analysename);
        selectedAnalysisType = study.getAnalysisTypes().stream().filter(analysis -> analysis.getAnalysisName().equals(analysename)).findFirst().get();
        int startRow = 11;
        Map<String,String> map = new HashMap<>();
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String barcode = "";
                try {
                    System.out.println("in barcode");
                    barcode = (String) getCellValue(row.getCell(3), cellType.STRING);
                } catch (IOException e) {
                    System.out.println(" in catch");
                    Double barcodeDouble = (Double) getCellValue(row.getCell(3), cellType.NUMERIC);
                    barcode = String.valueOf(barcodeDouble).split("\\.")[0];
                }
                System.out.println("before map");
                map.put(barcode, String.valueOf(row.getCell(6)));
            }
        }
        map.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        List<Analysis> relevantAnalyses = study.getListOfSamples().stream()
                .flatMap(sample -> sample.getListOfAnalysis().stream())
                .filter(analysis -> analysis.getAnalysisType().getId().equals(selectedAnalysisType.getId()))
                .toList();
        for (Map.Entry<String, String> entry : map.entrySet()){
            String barcode = entry.getKey();
            Analysis analysis = findCorrectAnalysis(relevantAnalyses,barcode);
            analysis.setAnalysisResult(String.valueOf(map.get(barcode)));
            analysisRepository.save(analysis);
        }
        workbook.close();
        inputStream.close();

    }

    public Analysis findCorrectAnalysis(List<Analysis> list, String barcode){
        return list.stream()
                .filter(analysis -> barcode.equals(analysis.getSample().getSample_barcode()))
                .findFirst().get();

    }
}
