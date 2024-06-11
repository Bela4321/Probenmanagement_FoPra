package de.unimarburg.samplemanagement.UI.create_study;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.UI.SidebarFactory;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/Studies")
public class StudiesView extends HorizontalLayout {
    List<Study> studies;
    Grid<Study> studyGrid;
    StudyRepository studyRepository;


    @Autowired
    public StudiesView(StudyRepository studyRepository, SidebarFactory sidebar) {
        this.studyRepository = studyRepository;
        add(sidebar.getSidebar());
        VerticalLayout mainContent = new VerticalLayout();
        loadStudies();
        Button button = new Button("refesh");
        button.addClickListener(e->{
            loadStudies();
        });
        Button button2 = new Button("Create Study");
        button2.addClickListener(e-> button2.getUI().ifPresent(ui -> ui.navigate("create_Study")));
        mainContent.add(studyGrid,new HorizontalLayout(button,button2));
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

        studyGrid = grid;
    }


}
