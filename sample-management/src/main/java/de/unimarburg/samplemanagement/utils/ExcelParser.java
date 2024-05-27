package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    public enum cellType {
        STRING, NUMERIC, DATE, BOOLEAN
    }

    public static Study readExcelFile(FileInputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Daten sind im ersten Blatt
        Iterator<Row> iterator = sheet.iterator();

        String studyName;
        List<Sample> sampleList = new ArrayList<>();

        // Reading first line
        if (iterator.hasNext()) {
            Row currentRow = iterator.next();
            studyName = (String) getCellValue(currentRow.getCell(1), cellType.STRING);
            if (iterator.hasNext()) {
                // Skip the header
                iterator.next();
            }
            else {
                throw new IOException("Wrong file format");
            }
        }
        else {
            throw new IOException("Wrong file format");
        }

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

            //Creating Sample Objects
            Sample sample = new Sample();
            String coordinates = (String) getCellValue(currentRow.getCell(0), cellType.STRING);
            sample.setCoordinates(coordinates);

            //TODO: ID vom subject ist auto-generated?
            double idDouble = (Double) getCellValue(currentRow.getCell(1), cellType.NUMERIC);
            int id = getNumericValue(idDouble);

            double visitDouble = (Double) getCellValue(currentRow.getCell(2), cellType.NUMERIC);
            int visit = getNumericValue(idDouble);
            sample.setVisits(visit);

            Date date = (Date) getCellValue(currentRow.getCell(3), cellType.DATE);
            sample.setSampleDate(date);

            String amount = (String) getCellValue(currentRow.getCell(4), cellType.STRING);
            sample.setSample_amount(amount);

            //TODO: Not in Sample Class
            String sampleType = (String) getCellValue(currentRow.getCell(5), cellType.STRING);
            sample.setCoordinates(coordinates);

            String barcode = (String) getCellValue(currentRow.getCell(6), cellType.STRING);
            sample.setSample_barcode(barcode);

            sampleList.add(sample);
        }

        workbook.close();
        inputStream.close();

        //TODO: Auto-generated ID -> NULL??   Date nicht in Tabelle (aktuelles Date nehmen?)
        Study study = new Study(null, studyName, null, sampleList);

        return study;
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
