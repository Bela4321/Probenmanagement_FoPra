package de.unimarburg.samplemanagement.UI.inputAnalysisResult;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextField;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.Parameter;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.AnalysisService;
import de.unimarburg.samplemanagement.service.ParameterService;
import de.unimarburg.samplemanagement.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("/input_analysis")
public class InputAnalysisResult extends VerticalLayout implements HasUrlParameter<String> {

    private final StudyService studyService;
    private final AnalysisService analysisService;
    private final ParameterService parameterService;
    private Grid<Sample> sampleGrid = new Grid<>(Sample.class);
    private Long studyID;
    private Map<Analysis, String> changes = new HashMap<>();
    private Parameter selectedParameter = null;
    private Grid<Analysis> grid;
    private HorizontalLayout buttonLayout;


    @Autowired
    public InputAnalysisResult(StudyService studyService, AnalysisService analysisService, ParameterService parameterService) {
        this.studyService = studyService;
        this.analysisService = analysisService;
        this.parameterService = parameterService;
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
            loadParameters();
        } else {
            add("Keine Studie ausgewählt. Bitte noch ein Mal versuchen.");
        }
    }

    // Read all available parameters for selected study and display them
    private void loadParameters() {
        if (studyID != null) {
            add("Parameter auswählen zum Eintragen der Analyseergebnisse");
            Study study = studyService.getStudyWithSamples(studyID);
            List<Sample> samples = study.getListOfSamples();
            HorizontalLayout parameterLayout = new HorizontalLayout();
            boolean param = false;

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
                    .collect(Collectors.toList());

            RadioButtonGroup<Parameter> radioButtonGroup = new RadioButtonGroup<>();
            radioButtonGroup.setLabel("Parameter");

            radioButtonGroup.setItems(uniqueParameters);
            radioButtonGroup.setItemLabelGenerator(Parameter::getParameterName);

            radioButtonGroup.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<RadioButtonGroup<Parameter>, Parameter>>) event -> {
                selectedParameter = event.getValue();
                loadEditView();
            });

            for (Parameter par : uniqueParameters) {
                if (par != null) {
                    param = true;
                }
            }
            if (!param) {
                add("Keine Analysen vorhanden für Studie: " + study.getStudyName());
                return;
            }
            parameterLayout.add(radioButtonGroup);
            add(parameterLayout);
        }
    }

    // Load the UI elements for editing analysis parameters
    private void loadEditView() {
        changes.clear();
        List<Analysis> analysisList = parameterService.getParameterWithAnalysis(selectedParameter.getId()).getListOfAnalysis();

        // Delete Layouts for updating View
        if (grid != null) {
            remove(grid);
        }
        if (buttonLayout != null) {
            remove(buttonLayout);
        }

        // Create a new grid to display the data
        grid = new Grid<>();
        grid.setItems(analysisList);
        grid.addColumn(analysis -> analysis.getSample().getSample_barcode()).setHeader("Barcode");
        Grid.Column<Analysis> resultColumn = grid.addColumn(Analysis::getAnalysisResult).setHeader("Ergebnis");

        // Setup the editor
        Editor<Analysis> editor = grid.getEditor();
        editor.setBuffered(false);

        // Editor for Analysis Result
        TextField resultField = new TextField();
        Binder<Analysis> binder = new Binder<>(Analysis.class);
        binder.forField(resultField)
                .withValidator(value -> value != null, "Result cannot be null")
                .bind(Analysis::getAnalysisResult, (analysis, value) -> {
                    String newValue = value.isEmpty() ? null : value;
                    changes.put(analysis, newValue); // Track changes
                    analysis.setAnalysisResult(newValue);
                });

        resultColumn.setEditorComponent(resultField);
        editor.setBinder(binder);

        // Start editing on double-click
        grid.addItemDoubleClickListener(event -> {
            editor.editItem(event.getItem());
            resultField.focus();
        });

        // Add the new grid to the layout
        add(grid);

        // Add save and cancel buttons
        Button saveButton = new Button("Speichern", e -> saveChanges());

        Button cancelButton = new Button("Abbrechen", e -> {
            editor.cancel();
            loadEditView();
        });

        buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveButton, cancelButton);
        add(buttonLayout);
    }

    // Save changes to Database
    // TODO: Date is set when the user clicks the save button. Change?
    private void saveChanges() {
        changes.forEach((analysis, value) -> {
            analysis.setAnalysisResult(value);
            analysis.setAnalysisDate(new Date());
            analysisService.saveAnalysis(analysis);
        });
        Notification.show("Alle Änderungen in Datenbank gespeichert");
        loadEditView(); // Refresh the view
    }
}