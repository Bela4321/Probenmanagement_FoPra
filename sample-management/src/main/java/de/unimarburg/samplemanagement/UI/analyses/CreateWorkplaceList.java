package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Route("/CreateWorkplaceList")
public class CreateWorkplaceList extends HorizontalLayout {
    private final ClientStateService clientStateService;
    private final ArrayList<Sample> selectedSampleBarcodes = new ArrayList<>();
    private LocalDate date;

    Anchor downloadLink = new Anchor("", "Download Arbeitsplatzliste");
    Div wrapperDiv = new Div(downloadLink);


    @Autowired
    public CreateWorkplaceList(ClientStateService clientStateService) {
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

        selectedSampleBarcodes.addAll(study.getListOfSamples());
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
                selectedSampleBarcodes.clear();
                selectedSampleBarcodes.addAll(e.getValue().getSamples());
            } else {
                sampleGrid.setItems(study.getListOfSamples());
                selectedSampleBarcodes.clear();
                selectedSampleBarcodes.addAll(study.getListOfSamples());
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

        wrapperDiv.addClickListener(event -> {
            wrapperDiv.setVisible(false);
        });
        body.add(wrapperDiv);
        wrapperDiv.setVisible(false);

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
        IntegerField maxProListeField = new IntegerField("Max. pro Liste");
        maxProListeField.setMin(1);
        IntegerField assayNrField = new IntegerField("First Plate ID");
        assayNrField.setValue(1);
        assayNrField.setMin(1);
        textFieldsLayout.add(
                new TextField("Operator Name/Kürzel"),
                new TextField("Final Calculation by"),
                assayNrField,
                maxProListeField
        );
        return textFieldsLayout;
    }

    private Button createReportButton(VerticalLayout body, DatePicker datePicker, RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout) {
        Button createReportButton = new Button("Arbeitsplatzlisten erstellen");

        createReportButton.addClickListener(event -> generateReport(body, datePicker, radioButtonGroup, textFieldsLayout));

        createReportButton.setEnabled(false);
        radioButtonGroup.addValueChangeListener(event -> createReportButton.setEnabled(event.getValue() != null));

        return createReportButton;
    }

    private void generateReport(VerticalLayout body, DatePicker datePicker, RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            date = datePicker.getValue();
            ArrayList<Sample> sampleList = new ArrayList<>();
            ArrayList<Sample> selectedSampleCopy = new ArrayList<>(selectedSampleBarcodes);

            IntegerField maxPerTableField = (IntegerField) textFieldsLayout.getComponentAt(3);
            int maxPerTable = Optional.ofNullable(maxPerTableField.getValue()).orElse(1000);
            int samplesCount = selectedSampleCopy.size();
            int tables = (samplesCount + maxPerTable - 1) / maxPerTable;  // Calculate number of tables
            int plateNrRead = Optional.ofNullable(((IntegerField) textFieldsLayout.getComponentAt(2)).getValue()).orElse(1);

            for (int i = 1; i <= tables; i++) {
                sampleList.clear();

                int plateNr = plateNrRead + i - 1;
                String protocolName = generateProtocolName(radioButtonGroup, textFieldsLayout, plateNr);
                Map<String, String> data = collectData(radioButtonGroup, textFieldsLayout, protocolName, plateNr);

                // Move elements from selectedSampleBarcodes to sampleList in batches
                int elementsToMove = Math.min(maxPerTable, selectedSampleCopy.size());

                for (int j = 0; j < elementsToMove; j++) {
                    sampleList.add(selectedSampleCopy.remove(0)); // Remove from selectedSampleBarcodes and add to sampleList
                }

                ByteArrayInputStream byteArrayInputStream = createExcelFile(data, sampleList);

                ZipEntry entry = new ZipEntry(validateFilename(protocolName) + ".xlsx");
                zos.putNextEntry(entry);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = byteArrayInputStream.read(buffer)) > -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                byteArrayInputStream.close();
            }

            zos.close();

            ByteArrayInputStream zipInputStream = new ByteArrayInputStream(baos.toByteArray());
            String zipFilename = "Protocols_" + date + ".zip";
            StreamResource resource = new StreamResource(zipFilename, () -> zipInputStream);

            downloadLink.setHref(resource);
            downloadLink.getElement().setAttribute("download", true);
            wrapperDiv.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
            body.add("Error generating report: " + e.getMessage());
        }
    }


    private String generateProtocolName(RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout, int i) {
        return date.toString() + "_" +
                Optional.ofNullable(radioButtonGroup.getValue()).orElse("") + "_" +
                "Plate ID" + "_" + i + "_" +
                Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(0)).getValue()).orElse("");
    }

    private Map<String, String> collectData(RadioButtonGroup<String> radioButtonGroup, HorizontalLayout textFieldsLayout, String protocolName, Integer plateNr) {
        return Map.of(
                "operatorName", Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(0)).getValue()).orElse(""),
                "calculatorName" , Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(1)).getValue()).orElse(""),
                "freeTextField", Optional.ofNullable(((TextField) textFieldsLayout.getComponentAt(1)).getValue()).orElse(""),
                "nr", plateNr.toString(),
                "assay", Optional.ofNullable(radioButtonGroup.getValue()).orElse(""),
                "maxProListe", Optional.ofNullable(((IntegerField) textFieldsLayout.getComponentAt(3)).getValue()).map(String::valueOf).orElse(""),
                "protocolName", protocolName
        );
    }

    private String validateFilename(String protocolName) {
        return protocolName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private ByteArrayInputStream createExcelFile(Map<String, String> data, ArrayList<Sample> sampleList) throws IOException {
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
            ExcelTemplateFiller.fillTemplate(templateInputStream, outputPath, data, sampleList, clientStateService.getClientState().getSelectedStudy(), date);

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
