package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.DISPLAY_UTILS;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import de.unimarburg.samplemanagement.utils.uploader.DownloadLinksArea;
import de.unimarburg.samplemanagement.utils.uploader.ExcelUploader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static de.unimarburg.samplemanagement.utils.ExcelParser.getCellValue;

@Route("/EnterSampleAnalysis")
public class InputAnalysisResult extends HorizontalLayout{
    private final SampleRepository sampleRepository;
    private ClientStateService clientStateService;
    private Study study;
    private AnalysisType selectedAnalysisType = null;
    ExcelParser excelParser;



    @Autowired
    public InputAnalysisResult(ClientStateService clientStateService, SampleRepository sampleRepository) {
        this.clientStateService = clientStateService;
        this.sampleRepository = sampleRepository;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        study = clientStateService.getClientState().getSelectedStudy();
        if (clientStateService.getClientState().getSelectedStudy() == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        add(loadContent());
    }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        File uploadFolder = getUploadFolder();
        ExcelUploader uploadArea = new ExcelUploader(uploadFolder);
        DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder, excelParser);
        Button processFileButton = new Button("Process Data");
        processFileButton.addClickListener(e -> {
            //Get uploaded file
            File selectedFile = uploadFolder.listFiles()[0];
            if (selectedFile == null) {
                Notification.show("No file uploaded");
                return;
            }
                try {
                    readAnalysisFile(new FileInputStream(selectedFile));
                } catch (IOException ex) {
                    Notification.show("Error processing file: " + ex.getMessage());
                    return;
                }

            });
        List<Button> analysisSelectionButtons = study.getAnalysisTypes().stream()
                .map(analysisType -> {
            Button button = new Button(analysisType.getAnalysisName());
            button.addClickListener(e -> {
                selectedAnalysisType = analysisType;
                body.removeAll();
                body.add(loadAnalysisTypeContent(), uploadArea, processFileButton);
            });
            return button;
        }).toList();
        body.add(DISPLAY_UTILS.getBoxAlignment(analysisSelectionButtons.toArray(new Button[0])));
        return body;
    }

    private Component loadAnalysisTypeContent() {
        Grid<Analysis> analysisGrid = new Grid<>();
        List<Analysis> relevantAnalyses = study.getListOfSamples().stream()
                        .flatMap(sample -> sample.getListOfAnalysis().stream())
                                .filter(analysis -> analysis.getAnalysisType().getId().equals(selectedAnalysisType.getId()))
                                        .toList();

        analysisGrid.setItems(relevantAnalyses);

        analysisGrid.addColumn(analysis -> analysis.getSample().getSample_barcode()).setHeader("Sample Barcode");
        //editable result column
        analysisGrid.addComponentColumn(analysis -> {
            TextField textField = new TextField();
            String analysisResult = analysis.getAnalysisResult();
            if (analysisResult==null) {
                analysisResult = "";
            }
            textField.setValue(analysisResult);
            textField.addValueChangeListener(e -> {
                saveNewAnalysisResult(analysis, e.getValue());
            });
            return textField;
        }).setHeader(selectedAnalysisType.getAnalysisName());



        return analysisGrid;
    }


    private void saveNewAnalysisResult(Analysis analysis, String value) {
        analysis.setAnalysisResult(value);
        sampleRepository.save(analysis.getSample());
        Notification.show("Ergebnis gespeichert");
    }



    private static File getUploadFolder() {
        File folder = new File("uploaded-files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public void readAnalysisFile(FileInputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator  = sheet.iterator();
        //Save the information to the Analysis for every row (sample) in the excel file
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            for (Analysis analysis : study.getListOfSamples().stream()
                    .flatMap(sample -> sample.getListOfAnalysis().stream())
                    .toList()) {
                if (analysis.getSample().getSample_barcode().equals(getCellValue(currentRow.getCell(1), ExcelParser.cellType.STRING))) {
                    analysis.setAnalysisResult((String) getCellValue(currentRow.getCell(4), ExcelParser.cellType.STRING));
                    sampleRepository.save(analysis.getSample());
                    Notification.show("Ergebnis gespeichert");
                }
            }
        }
    }

}