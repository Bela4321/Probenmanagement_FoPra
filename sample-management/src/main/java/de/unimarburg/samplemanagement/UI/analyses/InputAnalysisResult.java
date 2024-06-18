package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.Component;
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
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

}