package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.AnalysisTypeRepository;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/AddSampleAnalysisToStudy")
public class AddAnalysisToStudy extends HorizontalLayout {
    private final AnalysisTypeRepository analysisTypeRepository;
    private final StudyRepository studyRepository;
    ClientStateService clientStateService;
    Study study;
    Grid<AnalysisType> analysisTypeGrid;

    @Autowired
    public AddAnalysisToStudy(ClientStateService clientStateService, AnalysisTypeRepository analysisTypeRepository, StudyRepository studyRepository) {
        this.clientStateService = clientStateService;
        this.analysisTypeRepository = analysisTypeRepository;
        this.studyRepository = studyRepository;
        study = clientStateService.getClientState().getSelectedStudy();
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (clientStateService.getClientState().getSelectedStudy()== null) {
            add("Please select a Study");
            return;
        }

        add(loadContent());
    }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        //add new Analysis Type
        HorizontalLayout addAnalysis = new HorizontalLayout();
        TextField analysisName = new TextField("Analysis Name");
        TextField analysisDescription = new TextField("Analysis Description");
        Button addAnalysisButton = new Button("Create new Analysis Type");
        addAnalysisButton.addClickListener(buttonClickEvent -> {
            //check validity
            if (analysisName.isEmpty() || analysisTypeRepository.existsByAnalysisName(analysisName.getValue())) {
                Notification.show("Name of Analysis invalid/duplicate");
            }
            //add new analysis type
            AnalysisType analysisType = new AnalysisType();
            analysisType.setAnalysisName(analysisName.getValue());
            analysisType.setAnalysisDescription(analysisDescription.getValue());
            analysisType = analysisTypeRepository.save(analysisType);
            study.getAnalysisTypes().add(analysisType);

            studyRepository.save(study);
            analysisTypeGrid.setItems(analysisTypeRepository.findAll());
            Notification.show("Analysis Type added successfully");

        });
        addAnalysis.add(analysisName,analysisDescription, addAnalysisButton);
        body.add(addAnalysis);
        body.add(new Text("--------------------------"));

        analysisTypeGrid = new Grid<>();
        analysisTypeGrid.setItems(analysisTypeRepository.findAll());
        analysisTypeGrid.addColumn(AnalysisType::getAnalysisName).setHeader("Analysis Name");
        analysisTypeGrid.addColumn(AnalysisType::getAnalysisDescription).setHeader("Analysis Description");
        analysisTypeGrid.addComponentColumn(analysisType -> {
            Button button = new Button("Add Analysis");
            button.addClickListener(buttonClickEvent -> {
                study.getAnalysisTypes().add(analysisType);
                studyRepository.save(study);
                buttonClickEvent.getSource().setEnabled(false);
                buttonClickEvent.getSource().setText("already added");
                Notification.show("Analysis Type added successfully");
            });
            //disable button if analysis type is already in study
            if (study.getAnalysisTypes().contains(analysisType)) {
                button.setEnabled(false);
                button.setText("already added");
            }
            return button;
        }).setHeader("Add Analysis");
        body.add(analysisTypeGrid);

        return body;
    }
}
