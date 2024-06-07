package de.unimarburg.samplemanagement.UI.createStudyReport;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.Parameter;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.StudyService;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route("/create_study_report")
public class CreateStudyReport extends VerticalLayout implements HasUrlParameter<String> {


    private final StudyService studyService;
    private Grid<Sample> sampleGrid;
    private Long studyID;
    private Map<Long, Boolean> parameterCheckboxMap = new HashMap<>();

    @Autowired
    public CreateStudyReport(StudyService studyService) {
        this.studyService = studyService;
        sampleGrid = new Grid<>(Sample.class);
        studyID = null;
    }

    // Set the selected study
    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        try {
            studyID = Long.valueOf(s);
        } catch (NumberFormatException e) {
            studyID = null;
            Notification.show("Ungültige Studien-ID. Bitte überprüfen Sie die URL.");
        }
        if (studyID != null) {
            initLayout();
            loadData();
        }
        else {
            add("Keine Studie ausgewählt. Bitte noch ein Mal versuchen.");
        }
    }



    private void initLayout() {
        removeAll();
        sampleGrid.removeAllColumns();
    }

    private void loadData() {
        if (studyID != null) {
            Study study = studyService.getStudyWithSamples(studyID);
            List<Sample> samples = study.getListOfSamples();

            sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");

            // Handle samples with no analyses
            List<Analysis> allAnalyses = samples.stream()
                    .flatMap(sample -> {
                        List<Analysis> analyses = sample.getListOfAnalysis();
                        return analyses != null ? analyses.stream() : null;
                    })
                    .toList();

            // Collect all unique parameters from the analyses
            List<Parameter> uniqueParameters = allAnalyses.stream()
                    .map(Analysis::getParameter)
                    .distinct()
                    .toList();


            // Add columns for each unique analysis parameter
            boolean param = false;
            for (Parameter parameter : uniqueParameters) {
                if (parameter != null) {
                    param = true;
                    sampleGrid.addColumn(sample -> getAnalysisParameterForSample(sample, parameter.getId()))
                            .setHeader(parameter.getParameterName());
                }
            }

            if (!param) {
                add("Keine Analysen vorhanden für Studie: " + study.getStudyName());
                return;
            }

            add(sampleGrid);
            sampleGrid.setItems(samples);

            add("Parameter für den Ergebnisreport auswählen: ");

            // Add horizontal layout underneath the grid
            HorizontalLayout parameterLayout = new HorizontalLayout();
            for (Parameter parameter : uniqueParameters) {
                if (parameter != null) {
                    Checkbox checkbox = new Checkbox();
                    Div labelDiv = new Div(parameter.getParameterName());

                    // Initialize checkbox value and store it in the map
                    parameterCheckboxMap.put(parameter.getId(), checkbox.getValue());

                    checkbox.addValueChangeListener(event -> {
                        // Update the checkbox value in the map
                        parameterCheckboxMap.put(parameter.getId(), event.getValue());
                    });
                    parameterLayout.add(checkbox, labelDiv);
                }
            }
            add(parameterLayout);
            Button createReportButton = new Button("Bericht erstellen");
            add(createReportButton);
            createReportButton.addClickListener(event -> createReport());
        }
    }



    private String getAnalysisParameterForSample(Sample sample, Long parameterID) {
        try {
            return sample.getListOfAnalysis().stream()
                    .filter(analysis -> parameterID.equals(analysis.getParameter().getId()))
                    .map(Analysis::getAnalysisResult)
                    .findFirst()
                    .orElse("");
        } catch (Exception e) {
            return "Kein Analyseergebnis vorhanden";
        }
    }



    private void createReport() {
        boolean oneSelected = false;
        List<Long> idList = new ArrayList<>();
        if (parameterCheckboxMap.isEmpty()) {
            Notification.show("Keine Parameter vorhanden!");
            return;
        }
        for (Map.Entry<Long, Boolean> entry : parameterCheckboxMap.entrySet()) {
            if (entry.getValue()) {
                oneSelected = true;
                idList.add(entry.getKey());
            }
        }
        if (!oneSelected) {
            Notification.show("Keine Parameter ausgewählt!");
            return;
        }
        else {
            printReport(idList);
        }
    }

    private void printReport(List<Long> idList) {
        // Get all samples associated with the study
        Study study = studyService.getStudyWithSamples(studyID);
        List<Sample> samples = study.getListOfSamples();

        // Iterate through each sample
        for (Sample sample : samples) {
            // Iterate through each analysis of the sample
            for (Analysis analysis : sample.getListOfAnalysis()) {
                // Check if the analysis parameter ID is in idList
                if (idList.contains(analysis.getParameter().getId())) {
                    // Print information about the analysis
                    System.out.println("Barcode: " + sample.getSample_barcode());
                    System.out.println("Parameter: " + analysis.getParameter().getParameterName());
                    System.out.println("Analyseergebnis: " + analysis.getAnalysisResult());
                    System.out.println("Analysedatum: " + analysis.getAnalysisDate());

                    System.out.println("---------------------");
                }
            }
        }

    }

}

