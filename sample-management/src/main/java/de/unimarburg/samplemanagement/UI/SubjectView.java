package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.model.SubjectCompositeKey;
import de.unimarburg.samplemanagement.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route("/Manage_Subjects")
public class SubjectView extends VerticalLayout {

    private final SubjectService subjectService;
    private final Grid<Subject> grid = new Grid<>(Subject.class);
    private final TextField searchField = new TextField();
    private final Binder<Subject> binder = new Binder<>(Subject.class);
    private final Dialog dialog = new Dialog();

    @Autowired
    public SubjectView(SubjectService subjectService) {
        this.subjectService = subjectService;
        configureGrid();
        configureSearch();
        configureDialog();
        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.setColumns("id", "studyId");
        grid.asSingleSelect().addValueChangeListener(event -> editSubject(event.getValue()));
    }

    private void configureSearch() {
        searchField.setPlaceholder("Search...");
        searchField.addValueChangeListener(e -> updateList());
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add Subject");
        addButton.addClickListener(click -> addSubject());

        Button updateButton = new Button("Update Subject");
        updateButton.addClickListener(click -> updateSubject());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton, updateButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private void updateList() {
        grid.setItems(subjectService.searchByKeyword(searchField.getValue()));
    }

    private void addSubject() {
        grid.asSingleSelect().clear();
        editSubject(new Subject());
    }

    private void editSubject(Subject subject) {
        if (subject == null) {
            return;
        }
        binder.setBean(subject);

        dialog.removeAll();
        VerticalLayout dialogLayout = new VerticalLayout();

        TextField idField = new TextField("ID");
        binder.forField(idField)
                .withConverter(new StringToLongConverter("Must be a number"))
                .bind(Subject::getId, Subject::setId);

        TextField studyIdField = new TextField("Study ID");
        binder.forField(studyIdField)
                .withConverter(new StringToLongConverter("Must be a number"))
                .bind(Subject::getStudyId, Subject::setStudyId);

        dialogLayout.add(idField, studyIdField, createButtons(subject));
        dialog.add(dialogLayout);
        dialog.open();
    }

    private HorizontalLayout createButtons(Subject subject) {
        Button saveButton = new Button("Save", e -> saveSubject(subject));
        Button deleteButton = new Button("Delete", e -> deleteSubject(subject));
        return new HorizontalLayout(saveButton, deleteButton);
    }

    private void saveSubject(Subject subject) {
        Subject editedSubject = binder.getBean();
        Optional<Subject> existingSubjectOpt = subjectService.findById(editedSubject.getId(), editedSubject.getStudyId());

        if (existingSubjectOpt.isPresent()) {
            // Update existing subject
            subjectService.update(existingSubjectOpt.get(), editedSubject);
        } else {
            // Save new subject
            subjectService.save(editedSubject);
        }

        updateList();
        Notification.show("Subject saved");
        dialog.close();
    }

    private void deleteSubject(Subject subject) {
        subjectService.deleteById(subject.getId(), subject.getStudyId());
        updateList();
        Notification.show("Subject deleted");
        dialog.close();
    }

    private void updateSubject() {
        Subject selectedSubject = grid.asSingleSelect().getValue();
        if (selectedSubject != null) {
            editSubject(selectedSubject);
        } else {
            Notification.show("Please select a subject to update.");
        }
    }


    private void configureDialog() {
        dialog.setWidth("400px");
        dialog.setHeight("300px");
    }

    private static class StringToLongConverter implements Converter<String, Long> {
        private final String errorMessage;

        public StringToLongConverter(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public Result<Long> convertToModel(String value, ValueContext context) {
            try {
                return Result.ok(Long.parseLong(value));
            } catch (NumberFormatException e) {
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(Long value, ValueContext context) {
            return value == null ? "" : value.toString();
        }
    }
}
