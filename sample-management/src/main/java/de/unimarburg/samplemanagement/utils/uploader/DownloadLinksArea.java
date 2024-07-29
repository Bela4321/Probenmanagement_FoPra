package de.unimarburg.samplemanagement.utils.uploader;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class DownloadLinksArea extends VerticalLayout {

    private final File uploadFolder;
    private File selectedFile;
    ExcelParser excelParser;

    public DownloadLinksArea(File uploadFolder, ExcelParser excelParser) {
        this.uploadFolder = uploadFolder;
        this.excelParser = excelParser;
        refreshFileLinks();
        setMargin(true);
    }

    public void refreshFileLinks() {
        removeAll();
        add(new H4("Uploaded files:"));
        List<File> files = List.of(uploadFolder.listFiles());
        Grid<File> fileGrid = new Grid<>();
        fileGrid.setItems(files);
        fileGrid.addColumn(File::getName).setHeader("File Name");
        fileGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        fileGrid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                selectedFile = e.getFirstSelectedItem().get();
            }
        });
        //Add Button to delete all files from the folder
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new Button("Delete old files", e -> {
            for (File file : uploadFolder.listFiles()) {
                file.delete();
            }
            refreshFileLinks();
        }));
        horizontalLayout.add(new Button("Process Delivery", e -> {
            if (selectedFile == null) {
                Notification.show("Please select a file first");
                return;
            }
            try {
                //If current site is /EnterSampleAnalysis then call readAnalysislFile method
                excelParser.readExcelFile(getStream(selectedFile));
            } catch (IOException ex) {
                Notification.show("Error processing file: " + ex.getMessage());
                return;
            }
            Notification.show("File processed successfully");
            //Navigate to veriufication
            UI.getCurrent().navigate("VerifySampleDelivery");
        }));
        add(fileGrid, horizontalLayout);
    }


    private FileInputStream getStream(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stream;
    }
}