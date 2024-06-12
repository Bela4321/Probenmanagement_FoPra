package de.unimarburg.samplemanagement.UI.inputAnalysisResult;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextField;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.service.StudyService;
import de.unimarburg.samplemanagement.utils.DISPLAY_UTILS;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.apache.xmlbeans.impl.store.Saaj;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;
import java.util.stream.Collectors;

@Route("/EnterSampleAnalysis")
public class InputAnalysisResult extends HorizontalLayout{
    private final SampleRepository sampleRepository;
    private ClientStateService clientStateService;
    private Study study;
    private AnalysisType selectedAnalysisType = null;


    @Autowired
    public InputAnalysisResult(ClientStateService clientStateService, SampleRepository sampleRepository) {
        this.clientStateService = clientStateService;
        this.sampleRepository = sampleRepository;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));
        study = clientStateService.getUserState().getSelectedStudy();
        if (clientStateService.getUserState().getSelectedStudy() == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        add(loadContent());
    }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        List<Button> analysisSelectionButtons = study.getAnalysisTypes().stream()
                .map(analysisType -> {
            Button button = new Button(analysisType.getAnalysisName());
            button.addClickListener(e -> {
                selectedAnalysisType = analysisType;
                body.removeAll();
                body.add(loadAnalysisTypeContent());
            });
            return button;
        }).toList();
        body.add(DISPLAY_UTILS.geBoxAlignment(analysisSelectionButtons.toArray(new Button[0])));
        return body;
    }

    private Component loadAnalysisTypeContent() {
        Grid<Sample> sampleGrid = new Grid<>();
        sampleGrid.setItems(study.getListOfSamples());

        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");
        sampleGrid.addColumn(sample -> {
                    TextField resultField = new TextField();
                    resultField.setValue(GENERAL_UTIL.getAnalysisForSample(sample, selectedAnalysisType.getId()));
                    resultField.addValueChangeListener(e -> saveNewAnalysisResult(sample, e.getValue()));
                    return resultField;
                }).setHeader("Ergebnis");
        Button saveToDBButton = new Button("Speichern");
        saveToDBButton.addClickListener(e -> {
            saveAnalysesToDB();
            Notification.show("Daten gespeichert");
        });
        return sampleGrid;
    }

    private void saveAnalysesToDB() {
        sampleRepository.saveAll(study.getListOfSamples());
    }

    private void saveNewAnalysisResult(Sample sample, String value) {
        Analysis analysis = new Analysis();
        analysis.setAnalysisType(selectedAnalysisType);
        analysis.setAnalysisResult(value);
        analysis.setAnalysisDate(new Date());
        //is an old analysis result present?
        Analysis oldAnalysis = sample.getListOfAnalysis().stream()
                .filter(a -> Objects.equals(a.getAnalysisType().getId(), selectedAnalysisType.getId()))
                .findFirst().orElse(null);
        if (oldAnalysis != null) {
            sample.getListOfAnalysis().remove(oldAnalysis);
            //todo: save old analysis result to history?
        }
        sample.getListOfAnalysis().add(analysis);
    }

}