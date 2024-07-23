package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.SampleDelivery;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.ExcelTemplateFiller;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Route("/CreateAnalysisReport")
public class CreateAnalysisReport extends HorizontalLayout {
    private final ClientStateService clientStateService;
    private final ArrayList<String> selectedSampleBarcodes = new ArrayList<>();
    private LocalDate date;

    @Autowired
    public CreateAnalysisReport(ClientStateService clientStateService) {
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        add(loadData());
    }

    private VerticalLayout loadData() {
        VerticalLayout body = new VerticalLayout();

        Study study = clientStateService.getClientState().getSelectedStudy();

        if (study == null) {
            body.add("Bitte eine Studie auswählen");
            return body;
        }

        List<Sample> samples = study.getListOfSamples();
        Grid<Sample> sampleGrid = createSampleGrid(samples, study.getAnalysisTypes());

        // Dropdown filter for deliveries
        HorizontalLayout filterLayout = new HorizontalLayout();
        Select<SampleDelivery> deliveryFilter = new Select<>();
        deliveryFilter.setLabel("Filter by Delivery");
        deliveryFilter.setItems(study.getSampleDeliveryList());
        deliveryFilter.setEmptySelectionAllowed(true);
        deliveryFilter.setRenderer(new TextRenderer<>(sampleDelivery -> String.valueOf(sampleDelivery.getRunningNumber())));
        deliveryFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                sampleGrid.setItems(e.getValue().getSamples());
            } else {
                sampleGrid.setItems(study.getListOfSamples());
            }
        });
        filterLayout.add(deliveryFilter);
        body.add(filterLayout);

        body.add(sampleGrid);

        RadioButtonGroup<String> radioButtonGroup = createRadioButtonGroup(study.getAnalysisTypes());
        body.add(radioButtonGroup);

        DatePicker datePicker = createDatePicker();
        body.add(datePicker);

        HorizontalLayout textFieldsLayout = createTextFieldsLayout();
        body.add(textFieldsLayout);

        Button createReportButton = createReportButton(body, datePicker, radioButtonGroup, textFieldsLayout);
        body.add(createReportButton);

        return body;
    }

    private Grid<Sample> createSampleGrid(List<Sample> samples, List<AnalysisType> uniqueAnalysisTypes) {
        Grid<Sample> sampleGrid = new Grid<>();
        sampleGrid.setItems(samples);
        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");
        sampleGrid.addColumn(Sample::getSample_type).setHeader("Sample Type");
        sampleGrid.addColumn(Sample::getSample_amount).setHeader("Sample Amount");

        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            sampleGrid.addComponentColumn(sample -> {
                Checkbox checkbox = new Checkbox();
                boolean hasAnalysis = sample.getListOfAnalysis().stream()
                        .anyMatch(analysis -> analysis.getAnalysisType().getId().equals(analysisType.getId()));
                checkbox.setValue(hasAnalysis);
                checkbox.setReadOnly(true);
                return checkbox;
            }).setHeader(analysisType.getAnalysisName());
        }

        return sampleGrid;
    }

    private RadioButtonGroup<String> createRadioButtonGroup(List<AnalysisType> uniqueAnalysisTypes) {
        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setLabel("Assay");
        radioButtonGroup.setItems(uniqueAnalysisTypes.stream().map(AnalysisType::getAnalysisName).toArray(String[]::new));
        return radioButtonGroup;
    }

    private DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker("Datum");
        datePicker.setValue(LocalDate.now());
        return datePicker;
    }

    private HorizontalLayout createTextFieldsLayout() {
        HorizontalLayout textFieldsLayout = new HorizontalLayout();
        textFieldsLayout.add(
                new TextField("Operator Name/Kürzel"),
                new TextField("Freitext (Plate/Assay-ID etc.)"),
                new TextField("Plate/Assay-ID Nr."),
                new TextField("Verdünnung"),
                createMaxProListeField()
        );
        return textFieldsLayout;
    }

    private IntegerField createMaxProListeField() {
        IntegerField maxProListeField = new IntegerField("Max. pro Liste");
        maxProListeField.setMin(1);
        return maxProListeField;
    }

    private Button createReportButton(VerticalLayout body, DatePicker datePicker, RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout) {
        Button createReportButton = new Button("Arbeitsplatzlisten erstellen");

        createReportButton.addClickListener(event -> {
            try {
                date = datePicker.getValue();
                String protocolName = generateProtocolName(radioButtonGroup, textFieldsLayout);

                Map<String, String> data = collectData(radioButtonGroup, textFieldsLayout, protocolName);
                ByteArrayInputStream byteArrayInputStream = createExcelFile(data);

                String filename = validateFilename(protocolName) + ".xlsx";
                StreamResource resource = new StreamResource(filename, () -> byteArrayInputStream);

                Anchor downloadLink = new Anchor(resource, "Download Arbeitsplatzliste");
                downloadLink.getElement().setAttribute("download", true);
                body.add(downloadLink);
            } catch (IOException e) {
                e.printStackTrace();
                body.add("Error generating report: " + e.getMessage());
            }
        });

        createReportButton.setEnabled(false);
        radioButtonGroup.addValueChangeListener(event -> createReportButton.setEnabled(event.getValue() != null));

        return createReportButton;
    }

    private String generateProtocolName(RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout) {
        return date.toString() +
                Optional.ofNullable(radioButtonGroup.getValue()).orElse("") +
                Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(1)).getValue()).orElse("") +
                Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(2)).getValue()).orElse("") +
                Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(0)).getValue()).orElse("");
    }

    private Map<String, String> collectData(RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout, String protocolName) {
        return Map.of(
                "operatorName", Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(0)).getValue()).orElse(""),
                "freeTextField", Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(1)).getValue()).orElse(""),
                "nr", Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(2)).getValue()).orElse(""),
                "assay", Optional.ofNullable(radioButtonGroup.getValue()).orElse(""),
                "dilution", Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(3)).getValue()).orElse(""),
                "maxProListe", Optional.ofNullable(((IntegerField) textFieldsLayout.getComponentAt(4)).getValue()).map(String::valueOf).orElse(""),
                "protocolName", protocolName
        );
    }

    private String validateFilename(String protocolName) {
        return protocolName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private ByteArrayInputStream createExcelFile(Map<String, String> data) throws IOException {
        String templatePath = "AnalysisReportTemplate.xlsx";

        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (templateInputStream == null) {
                throw new FileNotFoundException("Template file not found: " + templatePath);
            }

            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String outputPath = "output/report.xlsx";
            ExcelTemplateFiller.fillTemplate(templateInputStream, outputPath, data, selectedSampleBarcodes, clientStateService.getClientState().getSelectedStudy(), date);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (FileInputStream fileInputStream = new FileInputStream(outputPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
            }
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }
}
