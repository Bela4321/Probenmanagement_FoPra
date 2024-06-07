package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.service.SampleService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Route("/Sample_View")
public class SampleView extends VerticalLayout {
    private final SampleService sampleService;
    private Grid<Sample> sampleGrid;
    private Sample selectedSample;

    public SampleView(SampleService sampleService) {
        this.sampleService = sampleService;
        initLayout();
    }

    private void initLayout() {
        add("List of Samples");

        // Search field for barcode
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search by Barcode...");
        searchField.addValueChangeListener(e -> refreshGrid(searchField.getValue()));
        add(searchField);

        Button addSampleButton = new Button("Add a new Sample");
        add(addSampleButton);

        sampleGrid = new Grid<>(Sample.class);
        sampleGrid.setColumns("id", "coordinates", "visits", "sampleDate", "sample_amount", "sample_barcode", "sample_type");
        add(sampleGrid);

        Button deleteSampleButton = new Button("Delete Selected Sample");
        deleteSampleButton.setEnabled(false); // Disable until a row is selected
        add(deleteSampleButton);

        sampleGrid.asSingleSelect().addValueChangeListener(event -> {
            selectedSample = event.getValue();
            deleteSampleButton.setEnabled(selectedSample != null);
        });

        deleteSampleButton.addClickListener(event -> {
            if (selectedSample != null) {
                deleteSelectedSample();
            }
        });

        addSampleButton.addClickListener(event -> openAddSampleDialog());

        refreshGrid();
    }

    private void openAddSampleDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");

        FormLayout formLayout = new FormLayout();
        TextField sampleNameField = new TextField("Subject ID");
        TextField coordinatesNameField = new TextField("Coordinates");
        IntegerField visitsNameField = new IntegerField("Visits");
        DatePicker sampleDatePicker = new DatePicker("Sample Date");
        sampleDatePicker.setValue(LocalDate.now());
        sampleDatePicker.setLocale(Locale.US);
        sampleDatePicker.setI18n(new DatePicker.DatePickerI18n().setDateFormat("MM/dd/yyyy"));
        TextField sampleAmountNameField = new TextField("Sample Amount");
        TextField sampleBarcodeNameField = new TextField("Sample Barcode");
        TextField sampleTypeNameField = new TextField("Sample Type");

        formLayout.add(sampleNameField,
                coordinatesNameField,
                visitsNameField,
                sampleDatePicker,
                sampleAmountNameField,
                sampleBarcodeNameField,
                sampleTypeNameField);

        Button saveButton = new Button("Save", e -> {
            // Get all the necessary fields
            String sampleName = sampleNameField.getValue();
            int visits = visitsNameField.getValue();
            LocalDate date = sampleDatePicker.getValue();

            // Convert LocalDate to Date
            Date sampleDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

            String sampleAmount = sampleAmountNameField.getValue();
            String sampleBarcode = sampleBarcodeNameField.getValue();
            String sampleType = sampleTypeNameField.getValue();

            // Create a new Sample instance and set its properties
            Sample newSample = new Sample();
            newSample.setCoordinates(sampleName);
            newSample.setVisits(visits);
            newSample.setSampleDate(sampleDate);
            newSample.setSample_amount(sampleAmount);
            newSample.setSample_barcode(sampleBarcode);
            newSample.setSample_type(sampleType);

            // Save the new sample using sampleService
            sampleService.save(newSample);

            // Refresh the grid to show the new sample
            refreshGrid();

            dialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.setAutofocus(true);
        formLayout.add(saveButton, cancelButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private void deleteSelectedSample() {
        // Delete the selected sample using sampleService
        sampleService.delete(selectedSample);

        // Refresh the grid to remove the deleted sample
        refreshGrid();
    }

    private void refreshGrid() {
        // Retrieve the updated list of samples and set it to the grid
        Collection<Sample> samples = sampleService.findAll();
        sampleGrid.setItems(samples);
    }

    private void refreshGrid(String filterText) {
        // Retrieve the filtered list of samples based on the search text
        Collection<Sample> samples = sampleService.findAll().stream()
                .filter(sample -> sample.getSample_barcode().contains(filterText))
                .collect(Collectors.toList());
        sampleGrid.setItems(samples);
    }
}
