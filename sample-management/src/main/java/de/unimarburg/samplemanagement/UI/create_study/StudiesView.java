package de.unimarburg.samplemanagement.UI.create_study;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.service.StudyService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/Studies")
public class StudiesView extends HorizontalLayout {
    List<Study> studies;
    Grid<Study> studyGrid;
    StudyRepository studyRepository;
    ClientStateService clientStateService;
    TextField filterName = new TextField();
    TextField filterDate = new TextField();
    private StudyService studyService;
    Grid<Study> grid;

    @Autowired
    public StudiesView(StudyRepository studyRepository, ClientStateService clientStateService,StudyService studyService) {
        this.studyRepository = studyRepository;
        this.clientStateService = clientStateService;
        this.studyService = studyService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));
        VerticalLayout mainContent = new VerticalLayout();
        loadStudies();
        Button button = new Button("Create Study");
        button.addClickListener(e-> button.getUI().ifPresent(ui -> ui.navigate("create_Study")));
        mainContent.add(getToolbar(),studyGrid,button);
        add(mainContent);
    }

    private void loadStudies() {
        //load studies from database
        studies = studyRepository.findAll();

        grid = new Grid<>();
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
    private HorizontalLayout getToolbar() {
        filterName.setPlaceholder("Filter by name...");
        filterName.setClearButtonVisible(true);
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterName.addValueChangeListener(e -> updateList());

        filterDate.setPlaceholder("Filter by date...");
        filterDate.setClearButtonVisible(true);
        filterDate.setValueChangeMode(ValueChangeMode.LAZY);
        filterDate.addValueChangeListener(e -> updateList());
        Button editStudy = new Button("Edit Study");
        editStudy.addClickListener(e-> UI.getCurrent().navigate("/edit_study"));

        var toolbar = new HorizontalLayout(filterName, filterDate,editStudy);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    private void updateList() {
        List<Study> studies = studyService.findByNameAndBirthDate(filterName.getValue(), filterDate.getValue());
//        studies = studies.stream()
//                .sorted((s1, s2) -> s1.getId().compareTo(s2.getId()))
//                .collect(Collectors.toList());

        grid.setItems(studies);
    }


}
