package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.ExcelTemplateFiller;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Route("/CreateAnalysisReport")
public class CreateAnalysisReport extends HorizontalLayout {
    private final ClientStateService clientStateService;
    private ArrayList<String> selectedSampleBarcodes = new ArrayList<>();
    LocalDate date;

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
        Grid<Sample> sampleGrid = new Grid<>();
        sampleGrid.setItems(samples);
        sampleGrid.setHeight("50%");
        List<AnalysisType> uniqueAnalysisTypes = study.getAnalysisTypes();

        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");

        sampleGrid.addComponentColumn(sample -> {
            Checkbox checkbox = new Checkbox();
            checkbox.addValueChangeListener(event -> {
                if (event.getValue()) {
                    selectedSampleBarcodes.add(sample.getSample_barcode());
                } else {
                    selectedSampleBarcodes.remove(sample.getSample_barcode());
                }
            });
            return checkbox;
        }).setHeader("In Arbeitsplatzliste");



        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            sampleGrid.addColumn(sample -> GENERAL_UTIL.getAnalysisForSample(sample, analysisType.getId()))
                    .setHeader(analysisType.getAnalysisName());
        }
        body.add(sampleGrid);

        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setLabel("Assay");
        radioButtonGroup.setItems(uniqueAnalysisTypes.stream().map(AnalysisType::getAnalysisName).toArray(String[]::new));
        body.add(radioButtonGroup);

        DatePicker datePicker = new DatePicker("Datum");
        datePicker.setValue(LocalDate.now());
        body.add(datePicker);

        HorizontalLayout textFieldsLayout = new HorizontalLayout();
        TextField operatorNameField = new TextField("Operator Name/Kürzel");
        TextField freeTextField = new TextField("Freitext (Plate/Assay-ID etc.)");
        TextField nrTextField = new TextField("Plate/Assay-ID Nr.");
        TextField dilutionTextField = new TextField("Verdünnung");
        textFieldsLayout.add(operatorNameField, freeTextField, nrTextField, dilutionTextField);
        body.add(textFieldsLayout);

        Button createReportButton = new Button("Arbeitsplatzlisten erstellen");

        //TODO: Only clickyble if analysis is selected
        createReportButton.addClickListener(event -> {
            try {
                date = datePicker.getValue();
                //TODO: Replace " " with "_"
                String protocolName = date.toString() + Optional.ofNullable(radioButtonGroup.getValue()).orElse("") +
                        Optional.ofNullable(freeTextField.getValue()).orElse("") + Optional.ofNullable(nrTextField.getValue()).orElse("") +
                        Optional.ofNullable(operatorNameField.getValue()).orElse("");

                Map<String, String> data = Map.of(
                        "operatorName", Optional.ofNullable(operatorNameField.getValue()).orElse(""),
                        "freeTextField", Optional.ofNullable(freeTextField.getValue()).orElse(""),
                        "nr", Optional.ofNullable(nrTextField.getValue()).orElse(""),
                        "assay", Optional.ofNullable(radioButtonGroup.getValue()).orElse(""),
                        "dilution", Optional.ofNullable(dilutionTextField.getValue()).orElse(""),
                        "protocolName", protocolName
                );
                ByteArrayInputStream byteArrayInputStream = createExcelFile(data);

                //TODO: Check valid filename
                String filename = protocolName + ".xlsx";

                StreamResource resource = new StreamResource(filename, () -> byteArrayInputStream);
                //TODO: Rework Download Button
                Anchor downloadLink = new Anchor(resource, "Download Arbeitsplatzliste");
                downloadLink.getElement().setAttribute("download", true);
                body.add(downloadLink);
            } catch (IOException e) {
                e.printStackTrace();
                body.add("Error generating report: " + e.getMessage());
            }
        });


        body.add(createReportButton);

        return body;
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
