package de.unimarburg.samplemanagement.UI.createStudyReport;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.unimarburg.samplemanagement.model.*;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.service.StudyService;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route("/CreateReport")
public class CreateStudyReport extends HorizontalLayout {


    private Grid<Sample> sampleGrid;
    private Study study;
    private Map<Long, Boolean> analysisCheckboxMap = new HashMap<>();
    private ClientStateService clientStateService;

    @Autowired
    public CreateStudyReport(ClientStateService clientStateService) {
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));

        this.clientStateService = clientStateService;

        sampleGrid = new Grid<>(Sample.class);
        if (clientStateService == null || clientStateService.getUserState().getSelectedStudy() == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        this.study = clientStateService.getUserState().getSelectedStudy();
        if (study == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        add(loadContent());
    }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        List<Sample> samples = study.getListOfSamples();

        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");

        List<AnalysisType> uniqueAnalysisTypes = study.getAnalysisTypes();

        // Add columns for each unique analysis
        if (uniqueAnalysisTypes.isEmpty()) {
            body.add("Keine Analysen vorhanden für Studie: " + study.getStudyName());
            return body;
        }
        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            sampleGrid.addColumn(sample -> GENERAL_UTIL.getAnalysisForSample(sample, analysisType.getId()))
                    .setHeader(analysisType.getAnalysisName());

        }

        body.add(sampleGrid);
        sampleGrid.setItems(samples);

        body.add("Parameter für den Ergebnisreport auswählen: ");

        // Add horizontal layout underneath the grid
        HorizontalLayout analysisSelection = new HorizontalLayout();
        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            Checkbox checkbox = new Checkbox();
            Div labelDiv = new Div(analysisType.getAnalysisName());

            // Initialize checkbox value and store it in the map
            analysisCheckboxMap.put(analysisType.getId(), checkbox.getValue());

            checkbox.addValueChangeListener(event -> {
                // Update the checkbox value in the map
                analysisCheckboxMap.put(analysisType.getId(), event.getValue());
            });
            analysisSelection.add(checkbox, labelDiv);

        }
        body.add(analysisSelection);
        Button createReportButton = new Button("Bericht erstellen");
        createReportButton.addClickListener(event -> createReport());
        body.add(createReportButton);

        return body;
    }



    private void createReport() {
        if (analysisCheckboxMap.values().stream().noneMatch(Boolean::booleanValue)) {
            Notification.show("Keine Parameter ausgewählt!");
            return;
        }

        List<Long> analysisTypeIdsToReport = new ArrayList<>();

        for (Map.Entry<Long, Boolean> entry : analysisCheckboxMap.entrySet()) {
            if (entry.getValue()) {
                analysisTypeIdsToReport.add(entry.getKey());
            }
        }
        printReport(analysisTypeIdsToReport);
    }

    private void printReport(List<Long> idList) {
        // Get all samples associated with the study
        List<Sample> samples = study.getListOfSamples();

        // Iterate through each sample
        for (Sample sample : samples) {
            // Iterate through each analysis of the sample
            for (Analysis analysis : sample.getListOfAnalysis()) {
                // Check if the analysis parameter ID is in idList
                if (idList.contains(analysis.getAnalysisType().getId())) {
                    // Print information about the analysis
                    System.out.println("Barcode: " + sample.getSample_barcode());
                    System.out.println("Analyse: " + analysis.getAnalysisType().getAnalysisName());
                    System.out.println("Analyseergebnis: " + analysis.getAnalysisResult());
                    System.out.println("Analysedatum: " + analysis.getAnalysisDate());

                    System.out.println("---------------------");
                }
            }
        }

    }

}

