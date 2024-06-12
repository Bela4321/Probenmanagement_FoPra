package de.unimarburg.samplemanagement.UI;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;
import de.unimarburg.samplemanagement.model.Study;

public class StudyForm extends FormLayout {
    BeanValidationBinder<Study> binder = new BeanValidationBinder<>(Study.class);
    TextField studyName = new TextField("Study Name");
    TextField studyDate = new TextField("Study Date");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    private Study study;

    public StudyForm() {
        binder.bindInstanceFields(this);
        // Add a label to show the date format pattern
        Span datePatternInfo = new Span("Enter date in format: Month dd,yyyy,HH:MM:SS AM/PM");
        add(studyName,
                studyDate,
                datePatternInfo,
                createButtonsLayout());
    }
    public void setStudy(Study study) {
        this.study = study;
        binder.setBean(study);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // Events
    public static abstract class StudyFormEvent extends ComponentEvent<StudyForm> {
        private Study study;

        protected StudyFormEvent(StudyForm source, Study study) {
            super(source, false);
            this.study = study;
        }

        public Study getStudy() {
            return study;
        }
    }

    public static class SaveEvent extends StudyFormEvent {
        SaveEvent(StudyForm source, Study study) {
            super(source, study);
        }
    }

    public static class DeleteEvent extends StudyFormEvent {
        DeleteEvent(StudyForm source, Study study) {
            super(source, study);
        }

    }

    public static class CloseEvent extends StudyFormEvent {
        CloseEvent(StudyForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}