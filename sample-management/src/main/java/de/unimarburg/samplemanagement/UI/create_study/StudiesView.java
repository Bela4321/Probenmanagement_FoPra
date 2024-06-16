package de.unimarburg.samplemanagement.UI.create_study;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/Studies")
public class StudiesView extends HorizontalLayout {
    List<Study> studies;
    Grid<Study> studyGrid;
    StudyRepository studyRepository;
    ClientStateService clientStateService;


    @Autowired
    public StudiesView(StudyRepository studyRepository, ClientStateService clientStateService) {
        this.studyRepository = studyRepository;
        this.clientStateService = clientStateService;

        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));
        VerticalLayout mainContent = new VerticalLayout();
        loadStudies();
        Button button = new Button("Create Study");
        button.addClickListener(e-> button.getUI().ifPresent(ui -> ui.navigate("create_Study")));
        mainContent.add(studyGrid,button);
        add(mainContent);
    }

    private void loadStudies() {
        //load studies from database
        studies = studyRepository.findAll();

        Grid<Study> grid = new Grid<>();
        grid.setItems(studies);
        grid.addColumn(Study::getStudyName).setHeader("Name");
        grid.addColumn(Study::getStudyDate).setHeader("Date of creation");
        grid.addColumn((study -> study.getListOfSamples().size())).setHeader("Number of samples");

        //allow selection of a study
        grid.addItemClickListener(e->{
            clientStateService.getUserState().setSelectedStudy(e.getItem());
            grid.getUI().ifPresent(ui -> ui.navigate("StudyOverview"));
        });

        studyGrid = grid;
    }


}
