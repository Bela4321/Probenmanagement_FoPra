package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExcelTemplateFiller {

    public static void fillTemplate(InputStream templateInputStream, String outputPath, Map<String, String> data, ArrayList<Sample> samples, Study study, LocalDate date) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(templateInputStream);
             FileOutputStream os = new FileOutputStream(outputPath)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            // Set the cell values based on the template structure

            sheet.getRow(2).getCell(5).setCellValue(study.getStudyName());
            sheet.getRow(4).getCell(3).setCellValue(date);
            sheet.getRow(5).getCell(3).setCellValue(data.get("operatorName"));


            sheet.getRow(7).getCell(1).setCellValue(data.get("freeTextField"));
            sheet.getRow(7).getCell(2).setCellValue(data.get("nr"));

            sheet.getRow(3).getCell(5).setCellValue(data.get("assay"));
            sheet.getRow(8).getCell(6).setCellValue(data.get("assay"));
            sheet.getRow(10).getCell(6).setCellValue(data.get("dilution"));


            //Copying Styles
            CellStyle sourceCellStyle0 = sheet.getRow(11).getCell(0).getCellStyle();
            CellStyle sourceCellStyle1 = sheet.getRow(11).getCell(1).getCellStyle();
            CellStyle sourceCellStyle2 = sheet.getRow(11).getCell(2).getCellStyle();
            CellStyle sourceCellStyle3 = sheet.getRow(11).getCell(3).getCellStyle();
            CellStyle sourceCellStyle4 = sheet.getRow(11).getCell(4).getCellStyle();
            CellStyle sourceCellStyle5 = sheet.getRow(11).getCell(5).getCellStyle();
            CellStyle sourceCellStyle6 = sheet.getRow(11).getCell(6).getCellStyle();

            //Extracring Barcodes
            ArrayList<String> barcodes = new ArrayList<>();
            for (Sample sample:samples) {
                barcodes.add(sample.getSample_barcode());
            }

            int count = 1;
            for (String barcode:barcodes) {
                int row = count + 10;
                List<Sample> sampleList = study.getListOfSamples();
                Sample sample = findByBarcode(barcode, sampleList);

                List<Analysis> analysisList = sample.getListOfAnalysis();
                Analysis analysis = findByName(data.get("assay"), analysisList);

                if (count > 1) {
                    Row currentRow = sheet.getRow(row);
                    if (currentRow == null) {
                        currentRow = sheet.createRow(row);
                    }

                    Cell cell0 = currentRow.createCell(0);
                    cell0.setCellStyle(sourceCellStyle0);
                    Cell cell1 = currentRow.createCell(1);
                    cell1.setCellStyle(sourceCellStyle1);
                    Cell cell2 = currentRow.createCell(2);
                    cell2.setCellStyle(sourceCellStyle2);
                    Cell cell3 = currentRow.createCell(3);
                    cell3.setCellStyle(sourceCellStyle3);
                    Cell cell4 = currentRow.createCell(4);
                    cell4.setCellStyle(sourceCellStyle4);
                    Cell cell5 = currentRow.createCell(5);
                    cell5.setCellStyle(sourceCellStyle5);
                    Cell cell6 = currentRow.createCell(6);
                    cell6.setCellStyle(sourceCellStyle6);

                    cell0.setCellValue(count);
                    cell2.setCellValue(Optional.ofNullable(sample.getCoordinates()).orElse(""));
                    cell3.setCellValue(Optional.ofNullable(sample.getSample_barcode()).orElse(""));
                    cell4.setCellValue(Optional.ofNullable(sample.getSample_amount()).orElse(""));
                    cell5.setCellValue(Optional.ofNullable(sample.getSample_type()).orElse(""));
                    cell6.setCellValue(Optional.ofNullable(analysis.getAnalysisResult()).orElse(""));

                }

                else {
                    sheet.getRow(row).getCell(0).setCellValue(count);
                    sheet.getRow(row).getCell(2).setCellValue(Optional.ofNullable(sample.getCoordinates()).orElse(""));
                    sheet.getRow(row).getCell(3).setCellValue(Optional.ofNullable(sample.getSample_barcode()).orElse(""));
                    sheet.getRow(row).getCell(4).setCellValue(Optional.ofNullable(sample.getSample_amount()).orElse(""));
                    sheet.getRow(row).getCell(5).setCellValue(Optional.ofNullable(sample.getSample_type()).orElse(""));
                    sheet.getRow(row).getCell(6).setCellValue(Optional.ofNullable(analysis.getAnalysisResult()).orElse(""));
                }

                count++;

            }



            // Write changes to the output file
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Sample findByBarcode(String barcode, List<Sample> list) {
        for (Sample s:list) {
            if (s.getSample_barcode().equals(barcode)) {
                return s;
            }
        }
        throw new RuntimeException("Sample findByBarcode failed!");
    }

    private static Analysis findByName(String name, List<Analysis> list) {
        for (Analysis s:list) {
            if (s.getAnalysisType().getAnalysisName().equals(name)) {
                return s;
            }
        }
        throw new RuntimeException("Analysis findByName failed!");
    }
}
