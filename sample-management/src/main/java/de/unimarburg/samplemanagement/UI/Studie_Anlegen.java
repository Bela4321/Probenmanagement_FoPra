package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.StudyService;

import java.util.List;
import java.util.stream.Collectors;

@Route("/Studie_Anlegen")
@CssImport("./Components/Styles.css")
public class Studie_Anlegen extends VerticalLayout {
    Grid grid = new Grid(Study.class);
    TextField filterName = new TextField();
    TextField filterDate = new TextField();
    private StudyService studyService;
    StudyForm studyForm;
    public Studie_Anlegen(StudyService studyService) {
//        add("Studien und Einsender in der Software mit den relevanten Daten „anlegen");
        this.studyService = studyService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(),getContent());
        updateList();
        closeEditor();
        setVisible(true);
    }
    private void configureGrid() {
        grid.setColumns("id","studyName","studyDate");
        grid.setSizeFull();
        grid.getColumns();
        List<Grid.Column> columns = grid.getColumns();
        columns.forEach(x -> x.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editStudy((Study) e.getValue()));
    }
    private void addStudy() {
        grid.asSingleSelect().clear();
        editStudy(new Study());
        updateList();
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
        studyService.save(updatedStudy);
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

        Button CreateNewStudy = new Button("Create New Study");
        CreateNewStudy.addClickListener(click -> addStudy());

        var toolbar = new HorizontalLayout(filterName, filterDate,CreateNewStudy);
        toolbar.addClassName("toolbar");
        return toolbar;
    }



    private void updateList() {
        List<Study> studies = studyService.findByNameAndBirthDate(filterName.getValue(), filterDate.getValue());
        System.out.println("update list method is called");

        // Apply sorting logic here if necessary to maintain order
        // For example, if sorting by 'id' or 'studyName':
        studies = studies.stream()
                .sorted((s1, s2) -> s1.getId().compareTo(s2.getId()))
                .collect(Collectors.toList());

        grid.setItems(studies);
    }


}
