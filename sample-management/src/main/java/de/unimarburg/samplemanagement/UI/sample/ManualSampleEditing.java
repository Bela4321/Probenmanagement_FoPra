package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.repository.SubjectRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.DoubleToLongConverter;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Optional;

@Route("/ManualSampleEditing")
public class ManualSampleEditing extends HorizontalLayout {
    ClientStateService clientStateService;
    SampleRepository sampleRepository;
    SubjectRepository subjectRepository;
    @Autowired
    public ManualSampleEditing(ClientStateService clientStateService, SampleRepository sampleRepository, SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
        this.clientStateService = clientStateService;
        this.sampleRepository = sampleRepository;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (clientStateService.getClientState().getSelectedStudy() == null) {
            add("Please select a study");
            return;
        }
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(getContent());//todo implement manual sample editing


        add(verticalLayout);
    }

    private VerticalLayout getContent() {
        VerticalLayout body = new VerticalLayout();
        Grid<Sample> sampleGrid = new Grid<>();
        List<Sample> samples = sampleRepository.getSampleByStudyId(clientStateService.getClientState().getSelectedStudy().getId());
        sampleGrid.setItems(samples);


        // Define columns
        Grid.Column<Sample> barcodeColumn = sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode").setSortable(true);
        Grid.Column<Sample> typeColumn = sampleGrid.addColumn(Sample::getSample_type).setHeader("Sample Type").setSortable(true);
        Grid.Column<Sample> amountColumn = sampleGrid.addColumn(Sample::getSample_amount).setHeader("Sample Amount").setSortable(true);
        Grid.Column<Sample> dateColumn = sampleGrid.addColumn(Sample::getSampleDate).setHeader("Sample Date").setSortable(true).setRenderer(GENERAL_UTIL.renderDate());
        Grid.Column<Sample> subjectIdColumn = sampleGrid.addColumn(sample -> {
            if (sample.getSubject() == null) {
                return null;
            }
            return sample.getSubject().getAlias();
        }).setHeader("Subject Alias").setSortable(true);
        Grid.Column<Sample> coordinatesColumn = sampleGrid.addColumn(Sample::getCoordinates).setHeader("Coordinates").setSortable(true);

        // Create the editor and its binder
        Editor<Sample> editor = sampleGrid.getEditor();
        Binder<Sample> binder = new Binder<>(Sample.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        // Define editor components for each column
        TextField barcodeField = new TextField();
        binder.bind(barcodeField, Sample::getSample_barcode, Sample::setSample_barcode);
        barcodeColumn.setEditorComponent(barcodeField);

        TextField typeField = new TextField();
        binder.bind(typeField, Sample::getSample_type, Sample::setSample_type);
        typeColumn.setEditorComponent(typeField);

        TextField amountField = new TextField();
        binder.bind(amountField, Sample::getSample_amount, Sample::setSample_amount);
        amountColumn.setEditorComponent(amountField);

        DatePicker dateField = new DatePicker();
        binder.forField(dateField)
                .withConverter(new LocalDateToDateConverter())
                .bind(Sample::getSampleDate, Sample::setSampleDate);
        dateColumn.setEditorComponent(dateField);

        NumberField subjectIdField = new NumberField();
        subjectIdField.setMin(0);
        subjectIdField.setStep(1);
        binder.forField(subjectIdField)
                .withConverter(new DoubleToLongConverter())
                .bind(sample -> {
                    if (sample.getSubject() == null) {
                        return null;
                    }
                    return sample.getSubject().getAlias();
                }, (sample, alias) -> {
                    Subject subject = subjectRepository.getSubjectByIdAndStudy(alias, clientStateService.getClientState().getSelectedStudy()).orElse(new Subject(alias,clientStateService.getClientState().getSelectedStudy()));
                    subjectRepository.save(subject);
                    sample.setSubject(subject);
                });
        subjectIdColumn.setEditorComponent(subjectIdField);

        TextField coordinatesField = new TextField();
        binder.bind(coordinatesField, Sample::getCoordinates, Sample::setCoordinates);
        coordinatesColumn.setEditorComponent(coordinatesField);

        //buttons
        Button saveButton = new Button("Save", event -> {
            editor.save();
        });
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setVisible(false);
        Button discardButton = new Button("Discard");
        discardButton.addClickListener(e-> {
            //if sample was new, remove it from the list
            if (editor.getItem().getId()==null){
                samples.remove(editor.getItem());
                sampleGrid.getDataProvider().refreshAll();
            }

            editor.cancel();
            saveButton.setVisible(false);
            discardButton.setVisible(false);
        });
        discardButton.addClickShortcut(Key.ESCAPE);
        discardButton.setVisible(false);

        // Add cancel listener to discard changes
        editor.addCancelListener(event -> {
            binder.readBean(null);
        });
        // Add save listener
        editor.addSaveListener(event -> {
            Sample editedSample = event.getItem();
            try {
                sampleRepository.save(editedSample);
            } catch (ValidationException e){
                Notification.show("Error saving item, please check if all necessary fields are filled in correctly");
                editor.editItem(editedSample); // Reopen the editor
                return;
            }
            saveButton.setVisible(false);
            discardButton.setVisible(false);
        });
        // Allow editing on double-click
        sampleGrid.addItemDoubleClickListener(event -> {
            //save currently edited item
            try {
                editor.save();
            } catch (ValidationException e) {
                Notification.show("Error saving item, changes are discarded");
                if (editor.getItem().getId()==null){
                    samples.remove(editor.getItem());
                    sampleGrid.getDataProvider().refreshAll();
                }
                editor.cancel();
            }
            //edit new item
            editor.editItem(event.getItem());

            saveButton.setVisible(true);
            discardButton.setVisible(true);
        });

        Button addButton = new Button("Add Sample", event -> {
            Sample newSample = new Sample();
            newSample.setStudy(clientStateService.getClientState().getSelectedStudy());
            samples.add(0,newSample); // Add the new sample to the list
            sampleGrid.getDataProvider().refreshAll(); // Refresh the grid

            // save the old item
            try {
                editor.save();
            } catch (ValidationException e) {
                Notification.show("Error saving item, changes are discarded");
                if (editor.getItem().getId()==null){
                    samples.remove(editor.getItem());
                    sampleGrid.getDataProvider().refreshAll();
                }
                editor.cancel();
            }
            //edit new item
            editor.editItem(newSample);

            saveButton.setVisible(true);
            discardButton.setVisible(true);
        });


        body.add(new HorizontalLayout(addButton, saveButton, discardButton));
        body.add(sampleGrid);
        return body;
    }

    private void setSubjectId(Sample sample, Long subjectId) {
        if (subjectId == null) {
            sample.setSubject(null);
            return;
        }
        Optional<Subject> subject = subjectRepository.getSubjectByIdAndStudy(subjectId, clientStateService.getClientState().getSelectedStudy());
        if (subject.isPresent()) {
            sample.setSubject(subject.get());
            return;
        }
        Subject subject1 = new Subject();
        subject1.setAlias(subjectId);
        subject1.setStudy(clientStateService.getClientState().getSelectedStudy());
        subjectRepository.save(subject1);
        sample.setSubject(subject1);

    }

}
