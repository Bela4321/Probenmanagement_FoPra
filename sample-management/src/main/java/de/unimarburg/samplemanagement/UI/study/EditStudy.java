package de.unimarburg.samplemanagement.UI.study;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.service.StudyService;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Route("/edit_study")
@CssImport("./Components/Styles.css")
public class EditStudy extends HorizontalLayout {
    Grid<Study> grid = new Grid();
    TextField filterName = new TextField();
    TextField filterDate = new TextField();
    private StudyService studyService;
    StudyForm studyForm;
    ClientStateService clientStateService;
    @Autowired
    StudyRepository studyRepository;

    public EditStudy(StudyService studyService, ClientStateService clientStateService) {
        this.studyService = studyService;
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        addClassName("list-view");
        setSizeFull();
        VerticalLayout content = new VerticalLayout();
        configureGrid();
        configureForm();
        content.add(getToolbar(),getContent());
        add(content);
        updateList();
        closeEditor();
        setVisible(true);
    }
    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Study::getStudyName).setHeader("Name");
        grid.addColumn(Study::getStudyDate).setHeader("Date of creation");
        grid.addColumn((study -> study.getListOfSamples().size())).setHeader("Number of samples");
        grid.asSingleSelect().addValueChangeListener(e -> editStudy((Study) e.getValue()));
    }

    private void closeEditor() {
        studyForm.setStudy(null);
        studyForm.setVisible(false);
        removeClassName("editing");
    }
    public void editStudy(Study study) {
        if (study == null) {
            closeEditor();
        } else {
            studyForm.setStudy(study);
            studyForm.setVisible(true);
            addClassName("editing");
        }
    }
    private void configureForm() {
        studyForm = new StudyForm();
        studyForm.setWidth("25em");

        studyForm.addSaveListener(this::saveStudy);
        studyForm.addDeleteListener(this::deleteStudy);
        studyForm.addCloseListener(e -> closeEditor());
    }
    private void saveStudy(StudyForm.SaveEvent event) {
        Study updatedStudy = event.getStudy();
        String studyName = event.getSource().studyName.getValue();
        LocalDate studyDate = event.getSource().studyDate.getValue();
        //studyService.save(updatedStudy);
        saveStudy(updatedStudy,studyName,studyDate);
        updateList();
        closeEditor();
    }

    private void deleteStudy(StudyForm.DeleteEvent event) {
        studyService.delete(event.getStudy());
        updateList();
        closeEditor();
    }
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, studyForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, studyForm);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
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

        var toolbar = new HorizontalLayout(filterName, filterDate);
        toolbar.addClassName("toolbar");
        return toolbar;
    }



    private void updateList() {
        List<Study> studies = studyService.findByNameAndBirthDate(filterName.getValue(), filterDate.getValue());
        studies = studies.stream()
                .sorted((s1, s2) -> s1.getId().compareTo(s2.getId()))
                .collect(Collectors.toList());

        grid.setItems(studies);
    }

    private void saveStudy(Study study,String studyname, LocalDate startdate) {
        //save study to database
        study.setStudyName(studyname);
        study.setStudyDate(GENERAL_UTIL.convertToDate(startdate));
        studyRepository.save(study);
        Notification.show("Study successfully stored");
    }
}
