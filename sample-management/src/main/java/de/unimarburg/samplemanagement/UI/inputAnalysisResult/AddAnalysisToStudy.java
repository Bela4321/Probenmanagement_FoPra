package de.unimarburg.samplemanagement.UI.inputAnalysisResult;

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

@Route("/AddSampleAnalysis")
public class AddAnalysisToStudy extends HorizontalLayout {
    private final AnalysisTypeRepository analysisTypeRepository;
    private final StudyRepository studyRepository;
    ClientStateService clientStateService;
    Study study;

    @Autowired
    public AddAnalysisToStudy(ClientStateService clientStateService, AnalysisTypeRepository analysisTypeRepository, StudyRepository studyRepository) {
        this.clientStateService = clientStateService;
        this.analysisTypeRepository = analysisTypeRepository;
        this.studyRepository = studyRepository;
        study = clientStateService.getUserState().getSelectedStudy();
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));
        if (clientStateService.getUserState().getSelectedStudy()== null) {
            add("Bitte eine Studie auswählen");
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
            Notification.show("Analysis Type added successfully");
        });
        addAnalysis.add(analysisName,analysisDescription, addAnalysisButton);
        body.add(addAnalysis);
        body.add(new Text("--------------------------"));
        //add previously used analysis
        List<AnalysisType> unusedAnalysisTypes = analysisTypeRepository.findAll();
        for (AnalysisType usedAnalysis : study.getAnalysisTypes()) {
            for (AnalysisType item : unusedAnalysisTypes) {
                if (item.getId().equals(usedAnalysis.getId())) {
                    unusedAnalysisTypes.remove(item);
                    break;
                }
            }
        }
        Grid<AnalysisType> analysisTypeGrid = new Grid<>(AnalysisType.class);
        analysisTypeGrid.setItems(unusedAnalysisTypes);
        analysisTypeGrid.addColumn(AnalysisType::getAnalysisName).setHeader("Analysis Name");
        analysisTypeGrid.addColumn(AnalysisType::getAnalysisDescription).setHeader("Analysis Description");
        analysisTypeGrid.addComponentColumn(analysisType -> {
            Button button = new Button("Add Analysis");
            button.addClickListener(buttonClickEvent -> {
                study.getAnalysisTypes().add(analysisType);
                studyRepository.save(study);
                Notification.show("Analysis Type added successfully");
            });
            button.setDisableOnClick(true);
            return button;
        }).setHeader("Add Analysis");
        body.add(analysisTypeGrid);

        return body;
    }
}
