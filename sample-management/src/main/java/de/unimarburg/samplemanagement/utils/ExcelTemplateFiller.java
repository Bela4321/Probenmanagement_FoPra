package de.unimarburg.samplemanagement.utils;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

public class ExcelTemplateFiller {

    public static void fillTemplate(InputStream templateInputStream, String outputPath, Map<String, String> data) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(templateInputStream);
             FileOutputStream os = new FileOutputStream(outputPath)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            // Set the cell values based on the template structure
            if (data.get("operatorName") != null) {
                sheet.getRow(5).getCell(3).setCellValue(data.get("operatorName"));
            }
            if (data.get("freeTextField") != null) {
                sheet.getRow(7).getCell(1).setCellValue(data.get("freeTextField")); // Row 6, Column B
            }
            if (data.get("assay") != null) {
                sheet.getRow(3).getCell(5).setCellValue(data.get("assay")); // Row 2, Column B
            }
            if (data.get("nr") != null) {
                sheet.getRow(7).getCell(2).setCellValue(data.get("nr")); // Row 6, Column C
            }

            // Write changes to the output file
            workbook.write(os);
            System.out.println("Template filled and saved to: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
