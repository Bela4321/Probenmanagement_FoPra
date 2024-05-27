package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Study;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelParser {

    public static Study readExcelFile(FileInputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Daten sind im ersten Blatt
        Iterator<Row> iterator = sheet.iterator();

        String studyName;

        // Reading first line
        if (iterator.hasNext()) {
            Row currentRow = iterator.next();
            studyName = currentRow.getCell(1).getStringCellValue();
            if (iterator.hasNext()) {
                // Skip the header
                iterator.next();
            }
            else {
                // Wrong File format
                return null;
            }
        }
        else {
            // Wrong File format
            return null;
        }

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            String name = currentRow.getCell(0).getStringCellValue();
            int age = (int) currentRow.getCell(1).getNumericCellValue();
            Person person = new Person(name, age);
            personList.add(person);
        }

        workbook.close();
        inputStream.close();

        return null;
    }
}
