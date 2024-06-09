package de.unimarburg.samplemanagement.UI.inputAnalysisResult;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.StudyService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/analysis_result")
public class AnalysisResultView extends VerticalLayout {

    private final StudyService studyService;
    private Grid<Study> studyGrid;

    private Study selectedStudy;

    @Autowired
    public AnalysisResultView(StudyService studyService) {
        this.studyService = studyService;
        initLayout();
        loadData();
    }

    private void initLayout() {
        // Add a description label
        add("Studie auswählen für das Eintragen der Analyseergebnisse");

        // Create buttons for creating individual or collective reports
        Button studySelectButton = new Button("Studie auswählen");

        // Add buttons to the layout
        add(studySelectButton);

        // Create and configure the grid to display study data
        studyGrid = new Grid<>(Study.class);
        studyGrid.setColumns("id", "studyName", "studyDate");
        add(studyGrid);

        // Add selection listener to the grid
        studyGrid.asSingleSelect().addValueChangeListener(event -> {
            Study selectedStudy = event.getValue();
            if (selectedStudy != null) {
                handleStudySelection(selectedStudy);
            }
        });

        studySelectButton.addClickListener(event -> studyConfirmed());
    }

    private void studyConfirmed() {
        if (selectedStudy != null) {
            Notification.show("Ausgewählte Studie: " + selectedStudy.getStudyName());
            getUI().ifPresent(ui -> ui.navigate("input_analysis/" + selectedStudy.getId()));

        } else {
            Notification.show("Bitte eine Studie auswählen!");
        }
    }

    private void loadData() {
        // Fetch the list of studies from the service
        List<Study> studies = studyService.getAllStudies();

        // Set the fetched data to the grid
        studyGrid.setItems(studies);
    }

    private void handleStudySelection(Study study) {
        selectedStudy = study;
    }
}

