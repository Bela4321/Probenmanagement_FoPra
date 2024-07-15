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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route("/CreateAnalysisReport")
public class CreateAnalysisReport extends HorizontalLayout {
    private final ClientStateService clientStateService;

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
        sampleGrid.addComponentColumn(sample -> new Checkbox()).setHeader("In Arbeitsplatzliste");

        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            sampleGrid.addColumn(sample -> "N/A").setHeader(analysisType.getAnalysisName());
        }
        body.add(sampleGrid);

        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setLabel("Analysis Type");
        radioButtonGroup.setItems(uniqueAnalysisTypes.stream().map(AnalysisType::getAnalysisName).toArray(String[]::new));
        body.add(radioButtonGroup);

        DatePicker datePicker = new DatePicker("Datum");
        datePicker.setValue(LocalDate.now());
        body.add(datePicker);

        HorizontalLayout textFieldsLayout = new HorizontalLayout();
        TextField operatorNameField = new TextField("Operator Name/Kürzel");
        TextField freeTextField = new TextField("Freitext (Plate/Assay-ID etc.)");
        TextField nrTextField = new TextField("Plate/Assay-ID Nr.");
        textFieldsLayout.add(operatorNameField, freeTextField, nrTextField);
        body.add(textFieldsLayout);

        Button createReportButton = new Button("Arbeitsplatzlisten erstellen");

        createReportButton.addClickListener(event -> {
            try {
                Map<String, String> data = Map.of(
                        "operatorName", Optional.ofNullable(operatorNameField.getValue()).orElse(""),
                        "freeTextField", Optional.ofNullable(freeTextField.getValue()).orElse(""),
                        "nr", Optional.ofNullable(nrTextField.getValue()).orElse(""),
                        "assay", Optional.ofNullable(radioButtonGroup.getValue()).orElse("")
                );
                ByteArrayInputStream byteArrayInputStream = createExcelFile(data);

                StreamResource resource = new StreamResource("report.xlsx", () -> byteArrayInputStream);
                Anchor downloadLink = new Anchor(resource, "Download Report");
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
            ExcelTemplateFiller.fillTemplate(templateInputStream, outputPath, data);

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
