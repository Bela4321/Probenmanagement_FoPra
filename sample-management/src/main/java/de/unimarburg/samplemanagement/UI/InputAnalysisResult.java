package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.grid.Grid;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.StudyService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/input_analysis")
public class InputAnalysisResult extends VerticalLayout {


    private final StudyService studyService;
    private Grid<Study> studyGrid;

    @Autowired
    public InputAnalysisResult(StudyService studyService) {
        this.studyService = studyService;
        initLayout();
        loadData();
    }

    private void initLayout() {
        // Add a description label
        add("Nach der Analyse einen „Befund“ (Sammel- oder Einzelbefund) erstellen");

        // Create buttons for creating individual or collective reports
        Button einzelBefundButton = new Button("Einzel_Befund_erstellen");
        Button sammelBefundButton = new Button("Sammel_Befund_erstellen");

        // Add buttons to the layout
        add(einzelBefundButton, sammelBefundButton);

        // Create and configure the grid to display study data
        studyGrid = new Grid<>(Study.class);
        studyGrid.setColumns("id", "studyName", "studyDate");
        add(studyGrid);
    }

    private void loadData() {
        // Fetch the list of studies from the service
        List<Study> studies = studyService.getAllStudies();

        // Set the fetched data to the grid
        studyGrid.setItems(studies);
    }
}

