package de.unimarburg.samplemanagement.UI.study;

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
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
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
        configureGridColumns();
        //allow selection of a study
        grid.addItemClickListener(e->{
            clientStateService.getClientState().setSelectedStudy(e.getItem());
            grid.getUI().ifPresent(ui -> ui.navigate("StudyOverview"));
        });

        studyGrid = grid;
    }

    private void configureGridColumns() {
        grid.addColumn(Study::getStudyName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Study::getStartDate).setHeader("Start Date").setAutoWidth(true);
        grid.addColumn(Study::getEndDate).setHeader("End Date").setAutoWidth(true);
        grid.addColumn((study -> study.getListOfSubjects().size())).setHeader("Number Of Subjects").setAutoWidth(true);
        grid.addColumn(study->study.getNumberOfSubjects()+"/"+study.getExpectedNumberOfSubjects()).setHeader("Expected Number Of Subjects").setAutoWidth(true);
        grid.addColumn(study-> study.getSampleDeliveryList().size()+"/"+study.getExpectedNumberOfSampeDeliveries()).setHeader("Number Of Sample Deliveries").setAutoWidth(true);
        grid.addColumn(Study::getSender1).setHeader("Sender1").setAutoWidth(true);
        grid.addColumn(Study::getSender2).setHeader("Sender2").setAutoWidth(true);
        grid.addColumn(Study::getSender3).setHeader("Sender3").setAutoWidth(true);
        grid.addColumn(Study::getSponsor).setHeader("Sponsor").setAutoWidth(true);
        grid.addColumn(Study::getRemark).setHeader("Remarks").setAutoWidth(true);
        grid.addColumn((study -> study.getListOfSamples().size())).setHeader("Number of samples").setAutoWidth(true);
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
