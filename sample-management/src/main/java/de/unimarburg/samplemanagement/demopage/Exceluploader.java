package de.unimarburg.samplemanagement.demopage;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
public class Exceluploader extends VerticalLayout{

    private final Upload uploadField;
    private final Span errorField;
    public Exceluploader(File uploadFile) {
        H4 title = new H4("Upload spreadsheet");
        Paragraph hint = new Paragraph(
                "File size must be less than or equal to 1 MB. Only Excel and CSV files are accepted.");
        uploadField = new Upload(createFileReceiver(uploadFile));
        uploadField.setAcceptedFileTypes(
                // Microsoft Excel (.xls)
                "application/vnd.ms-excel", ".xls",
                // Microsoft Excel (OpenXML, .xlsx)
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ".xlsx",
                // Comma-separated values (.csv)
                "text/csv", ".csv");
        uploadField.setMaxFiles(100);
        uploadField.setMaxFileSize(1024 * 1024);
        add("(max 1MB)");
        errorField = new Span();
        errorField.setVisible(false);
        errorField.getStyle().set("color", "red");

        uploadField.addFailedListener(e -> showErrorMessage(e.getReason().getMessage()));
        uploadField.addFileRejectedListener(e -> showErrorMessage(e.getErrorMessage()));

        add(title, hint, uploadField, errorField);
    }

    public Upload getUploadField() {
        return uploadField;
    }

    public void hideErrorField() {
        errorField.setVisible(false);
    }

    private Receiver createFileReceiver(File uploadFolder) {
        return (MultiFileReceiver) (filename, mimeType) -> {
            File file = new File(uploadFolder, filename);
            //Only allow uploading of Excel files
            if (!mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return null;
            }
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }
        };
    }
    private void showErrorMessage(String message) {
        errorField.setVisible(true);
        errorField.setText(message);
    }
}
