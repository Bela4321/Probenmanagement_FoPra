package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.StudyService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/create_study_report")
public class CreateStudyReport extends VerticalLayout implements HasUrlParameter<String> {


    private final StudyService studyService;
    private Grid<Study> studyGrid;
    private Study selectedStudy;
    private Long studyID;

    @Autowired
    public CreateStudyReport(StudyService studyService) {
        this.studyService = studyService;
        studyID = null;
        add("Keine Studie ausgewählt. Bitte noch ein Mal versuchen.");
        //initLayout();
        //loadData();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        if (s == null || s.isEmpty()) {
            studyID = null;
            Notification.show("Keine Studien-ID angegeben. Bitte geben Sie eine Studien-ID in der URL an.");
        } else {
            try {
                studyID = Long.valueOf(s);
            } catch (NumberFormatException e) {
                studyID = null;
                Notification.show("Ungültige Studien-ID. Bitte überprüfen Sie die URL.");
            }
        }
        initLayout(); // Re-initialize layout with new study ID or null
        //loadData(); // Load data based on the new study ID or null
    }



    private void initLayout() {
        removeAll();
        if (studyID != null) {
            // Add a description label
            add("Nach der Analyse einen „Befund“ (Sammel- oder Einzelbefund) erstellen");
        }
        else {
            add("Keine Studie ausgewählt. Bitte noch ein Mal versuchen.");
        }



    }


    private void loadData() {
        // Fetch the list of studies from the service
        List<Study> studies = studyService.getAllStudies();

        // Set the fetched data to the grid
        studyGrid.setItems(studies);
    }



}

