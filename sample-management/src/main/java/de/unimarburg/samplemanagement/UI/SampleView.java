package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.SampleService;
import de.unimarburg.samplemanagement.service.SubjectService;
import de.unimarburg.samplemanagement.service.StudyService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringComponent
@Route("/Manage_Samples")
public class SampleView extends VerticalLayout {

    private final SampleService sampleService;
    private final SubjectService subjectService;
    private final StudyService studyService;

    private Grid<Sample> grid = new Grid<>(Sample.class);
    private Button addButton = new Button("Add Sample");
    private Button editButton = new Button("Edit");
    private Button deleteButton = new Button("Delete");

    private Dialog sampleDialog = new Dialog();
    private TextField coordinates = new TextField("Coordinates");
    private TextField visits = new TextField("Visits");
    private DatePicker sampleDate = new DatePicker("Sample Date");
    private TextField sampleAmount = new TextField("Sample Amount");
    private TextField sampleBarcode = new TextField("Sample Barcode");
    private TextField sampleType = new TextField("Sample Type");
    private ComboBox<Subject> subjectComboBox = new ComboBox<>("Subject");
    private ComboBox<Study> studyComboBox = new ComboBox<>("Study");
    private Button saveSampleButton = new Button("Save");
    private Button cancelSampleButton = new Button("Cancel");

    private Binder<Sample> binder = new Binder<>(Sample.class);
    private Sample currentSample;

    @Autowired
    public SampleView(SampleService sampleService, SubjectService subjectService, StudyService studyService) {
        this.sampleService = sampleService;
        this.subjectService = subjectService;
        this.studyService = studyService;
        add(createToolbar(), grid, addButton);
        configureGrid();
        configureForm();
        configureSampleDialog();
    }

    @PostConstruct
    private void init() {
        updateGrid();
        configureComboboxes();
        binder.bindInstanceFields(this);
    }

    private void configureGrid() {
        grid.setColumns("id", "coordinates", "visits", "sampleDate", "sample_amount", "sample_barcode", "sample_type");
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                currentSample = event.getValue();
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                clearForm();
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });
    }

    private void configureForm() {
        addButton.addClickListener(e -> {
            clearForm();
            saveSampleButton.setText("Save");
            sampleDialog.open();
        });

        editButton.setEnabled(false);
        editButton.addClickListener(e -> {
            if (currentSample != null) {
                binder.readBean(currentSample);
                saveSampleButton.setText("Update");
                sampleDialog.open();
            } else {
                Notification.show("Please select a sample to edit");
            }
        });

        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> {
            if (currentSample != null) {
                sampleService.deleteSample(currentSample.getId());
                grid.setItems(sampleService.getAllSamples());
                clearForm();
                Notification.show("Sample deleted successfully");
            } else {
                Notification.show("Please select a sample to delete");
            }
        });
    }

    private void configureComboboxes() {
        List<Subject> subjects = subjectService.getAllSubjects();
        subjectComboBox.setItems(subjects);
        subjectComboBox.setItemLabelGenerator(Subject::toString); // Assuming Subject has a getName() method for the display name

        List<Study> studies = studyService.getAllStudies();
        studyComboBox.setItems(studies);
        studyComboBox.setItemLabelGenerator(Study::getStudyName); // Assuming Study has a getName() method for the display name
    }

    private void configureSampleDialog() {
        sampleDialog.setModal(true);
        sampleDialog.setCloseOnEsc(true);
        sampleDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(coordinates, visits, sampleDate, sampleAmount, sampleBarcode, sampleType, subjectComboBox, studyComboBox);
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, coordinates, visits, sampleDate, sampleAmount, sampleBarcode, sampleType, subjectComboBox, studyComboBox);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveSampleButton, cancelSampleButton);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.add(buttonLayout);

        sampleDialog.add(dialogLayout);

        saveSampleButton.addClickListener(e -> {
            if (saveSampleButton.getText().equals("Save")) {
                saveSample();
            } else if (saveSampleButton.getText().equals("Update")) {
                updateSample(currentSample);
            }
            sampleDialog.close();
        });

        cancelSampleButton.addClickListener(e -> {
            sampleDialog.close();
            clearForm();
        });
    }

    private void saveSample() {
        Sample sample = new Sample();

        if (binder.writeBeanIfValid(sample)) {
            Subject selectedSubject = subjectComboBox.getValue();
            if (selectedSubject != null) {
                sample.setSubject(selectedSubject);
            }

            Study selectedStudy = studyComboBox.getValue();
            if (selectedStudy != null) {
                sample.setStudy(selectedStudy);
            }

            sampleService.saveSample(sample);
            updateGrid();
            clearForm();
            Notification.show("Sample added successfully");
        } else {
            Notification.show("Please fill out all required fields.");
        }
    }

    private void updateSample(Sample sample) {
        if (binder.writeBeanIfValid(sample)) {
            Subject selectedSubject = subjectComboBox.getValue();
            if (selectedSubject != null) {
                sample.setSubject(selectedSubject);
            }

            Study selectedStudy = studyComboBox.getValue();
            if (selectedStudy != null) {
                sample.setStudy(selectedStudy);
            }

            sampleService.saveSample(sample);
            updateGrid();
            clearForm();
            Notification.show("Sample updated successfully");
        } else {
            Notification.show("Please fill out all required fields.");
        }
    }

    private void updateGrid() {
        grid.setItems(sampleService.getAllSamples());
    }

    private void clearForm() {
        binder.readBean(new Sample());
        currentSample = null;
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveSampleButton.setText("Save");
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.add(addButton, editButton, deleteButton);
        return toolbar;
    }
}
