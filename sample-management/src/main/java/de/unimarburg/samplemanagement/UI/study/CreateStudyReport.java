package de.unimarburg.samplemanagement.UI.study;

import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("/CreateReport")
public class CreateStudyReport extends HorizontalLayout {

    private Study study;
    private Map<Long, Boolean> analysisCheckboxMap = new HashMap<>();
    private ClientStateService clientStateService;
    private List<String[]> senders = new ArrayList<>();
    private String recipientAddress;
    private TextArea recipientAddressField;
    private Button printPdfButton;
    private Button saveButton;
    private Button editButton;

    private Anchor downloadLink;

    @Autowired
    public CreateStudyReport(ClientStateService clientStateService) {
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (clientStateService == null || clientStateService.getClientState().getSelectedStudy() == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        this.study = clientStateService.getClientState().getSelectedStudy();
        if (study == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        add(loadContent());
    }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        List<Sample> samples = study.getListOfSamples();

        Grid<Sample> sampleGrid = new Grid<>();

        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");

        List<AnalysisType> uniqueAnalysisTypes = study.getAnalysisTypes();

        // Add columns for each unique analysis
        if (uniqueAnalysisTypes.isEmpty()) {
            body.add("Keine Analysen vorhanden für Studie: " + study.getStudyName());
            return body;
        }
        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            sampleGrid.addColumn(sample -> GENERAL_UTIL.getAnalysisForSample(sample, analysisType.getId()))
                    .setHeader(analysisType.getAnalysisName());
        }

        body.add(sampleGrid);
        sampleGrid.setItems(samples);

        body.add("Parameter für den Ergebnisreport auswählen: ");

        // Add horizontal layout underneath the grid
        HorizontalLayout analysisSelection = new HorizontalLayout();
        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            Checkbox checkbox = new Checkbox();
            Div labelDiv = new Div(analysisType.getAnalysisName());

            // Initialize checkbox value and store it in the map
            analysisCheckboxMap.put(analysisType.getId(), checkbox.getValue());

            checkbox.addValueChangeListener(event -> {
                // Update the checkbox value in the map
                analysisCheckboxMap.put(analysisType.getId(), event.getValue());
            });
            analysisSelection.add(checkbox, labelDiv);
        }
        body.add(analysisSelection);

        Button addSenderButton = new Button("Add Sender Details");
        addSenderButton.addClickListener(event -> openSenderDialog());
        body.add(addSenderButton);

        recipientAddressField = new TextArea("Recipient Address");
        recipientAddressField.setWidthFull();
        recipientAddressField.setHeight("150px");
        recipientAddressField.addValueChangeListener(event -> saveButton.setEnabled(!recipientAddressField.getValue().trim().isEmpty()));
        body.add(recipientAddressField);

        saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            recipientAddress = recipientAddressField.getValue();
            recipientAddressField.setReadOnly(true);
            saveButton.setEnabled(false);
            editButton.setEnabled(true);
            checkEnablePrintButton();
        });
        saveButton.setEnabled(false);
        body.add(saveButton);

        editButton = new Button("Edit");
        editButton.addClickListener(event -> openEditDialog());
        editButton.setEnabled(false);
        body.add(editButton);

        printPdfButton = new Button("Bericht Erstellen");
        printPdfButton.setEnabled(false);
        printPdfButton.addClickListener(event -> {
            if (validateInputs()) {
                try {
                    // Generate the PDF file
                    String dest = "study_report.pdf";
                    generatePdf(dest);

                    // Create a StreamResource for downloading the generated PDF
                    StreamResource resource = new StreamResource("study_report.pdf", () -> {
                        try {
                            return new ByteArrayInputStream(java.nio.file.Files.readAllBytes(Paths.get(dest)));
                        } catch (IOException e) {
                            Notification.show("Error generating PDF: " + e.getMessage());
                            return null;
                        }
                    });

                    // Add the download link to the UI
                    if (downloadLink != null) {
                        remove(downloadLink);
                    }
                    downloadLink = new Anchor(resource, "");
                    downloadLink.getElement().setAttribute("download", true);
                    Button downloadButton = new Button("Download PDF");
                    downloadLink.add(downloadButton);
                    body.add(downloadLink);
                } catch (IOException | URISyntaxException e) {
                    Notification.show("Error generating PDF: " + e.getMessage());
                }
            } else {
                Notification.show("Please fill in all required fields.");
            }
        });
        body.add(printPdfButton);

        return body;
    }


    private void openEditDialog() {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();
        TextArea editRecipientAddressField = new TextArea("Recipient Address");
        editRecipientAddressField.setWidth("400px");
        editRecipientAddressField.setHeight("400px");
        editRecipientAddressField.setWidthFull();
        editRecipientAddressField.setHeight("150px");
        editRecipientAddressField.setValue(recipientAddress);

        Button saveEditButton = new Button("Save", e -> {
            recipientAddress = editRecipientAddressField.getValue();
            recipientAddressField.setValue(recipientAddress);
            recipientAddressField.setReadOnly(true);
            saveButton.setEnabled(false);
            editButton.setEnabled(true);
            checkEnablePrintButton();
            dialog.close();
        });

        formLayout.add(editRecipientAddressField, saveEditButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private void openSenderDialog() {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();
        TextField nameField = new TextField("Name");
        TextField positionField = new TextField("Position");
        Button addButton = new Button("Add Sender", e -> {
            senders.add(new String[]{nameField.getValue(), positionField.getValue()});
            dialog.close();
            checkEnablePrintButton();
        });
        formLayout.add(nameField, positionField, addButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private boolean validateInputs() {
        recipientAddress = recipientAddressField.getValue();
        return !recipientAddress.isEmpty() && !senders.isEmpty();
    }

    private void checkEnablePrintButton() {
        printPdfButton.setEnabled(validateInputs());
    }

    /*private void openFileChooser() {
        UI.getCurrent().getPage().executeJs(
                "var input = document.createElement('input'); " +
                        "input.type = 'file'; " +
                        "input.accept = '.pdf'; " +
                        "input.onchange = function(event) { " +
                        "  var file = event.target.files[0]; " +
                        "  $0.$server.handleFileSelection(file.name); " +
                        "}; " +
                        "input.click();", getElement());
    }

    public void handleFileSelection(String filePath) {
        try {
            generatePdf(filePath);
            Notification.show("PDF generated successfully at " + filePath);
        } catch (Exception e) {
            Notification.show("Error generating PDF: " + e.getMessage());
        }
    }*/

    public class HeaderFooterHandler implements IEventHandler {
        protected Image logo;

        public HeaderFooterHandler(Image logo) {
            this.logo = logo;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());
            Canvas canvas = new Canvas(pdfCanvas, pdfDoc, pdfDoc.getDefaultPageSize());

            // Add the logo
            logo.setFixedPosition((pdfDoc.getDefaultPageSize().getWidth() - logo.getImageScaledWidth()) / 2,
                    pdfDoc.getDefaultPageSize().getTop() - logo.getImageScaledHeight() - 10);
            canvas.add(logo);
            canvas.close();
        }
    }

    private void generatePdf(String dest) throws IOException, URISyntaxException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDoc);

        // Load the Calibri font from the resources directory
        String calibriFontPath = Paths.get(getClass().getClassLoader().getResource("calibri.ttf").toURI()).toString();
        PdfFont calibriFont = PdfFontFactory.createFont(calibriFontPath, PdfEncodings.IDENTITY_H, true);
        PdfFont calibriBoldFont = PdfFontFactory.createFont(calibriFontPath, PdfEncodings.IDENTITY_H, true);

        // Load the logo
        String logoPath = Paths.get(getClass().getClassLoader().getResource("uni-logo.png").toURI()).toString();
        ImageData logoImageData = ImageDataFactory.create(logoPath);
        Image logoImage = new Image(logoImageData).scaleToFit(100, 100).setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Set the header handler
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterHandler(logoImage));

        // Set the default font for the document
        document.setFont(calibriFont);
        document.setFontSize(11);

        // Add spacing after logo
        document.add(new Paragraph("\n"));

        // Add the header
        Paragraph header = new Paragraph("Philipps-Universität Marburg -- Institut für Virologie -- Immunmonitoringlabor")
                .setFont(calibriBoldFont)
                .setFontSize(11)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(header);

        // Add a horizontal line separator
        document.add(new LineSeparator(new SolidLine()));

        // Create the three-column table for sender details and fixed address
        float[] columnWidths = {2, 3, 5};
        Table senderTable = new Table(columnWidths);
        senderTable.setWidth(UnitValue.createPercentValue(100));

        // Add sender details and fixed address
        for (int i = 0; i < senders.size(); i++) {
            String[] sender = senders.get(i);
            senderTable.addCell(new Cell().add(new Paragraph(sender[0]).setFont(calibriBoldFont).setFontSize(11)).setBold().setBorder(null));
            senderTable.addCell(new Cell().add(new Paragraph(sender[1]).setFont(calibriBoldFont).setFontSize(11)).setBorder(null));

            if (i == 0) {
                senderTable.addCell(new Cell(senders.size(), 1).add(new Paragraph("Hans-Meerwein Straße 2\n35043 Marburg, Germany\nPhone: ++ 49 6421 28-65158\nE-Mail: immunmonitoring.labor@uni-marburg.de")
                                .setFont(calibriFont)
                                .setFontSize(11))
                        .setBorder(null));
            }
        }

        document.add(senderTable);

        // Add a horizontal line separator
        document.add(new LineSeparator(new SolidLine()));

        // Add recipient address
        Paragraph recipient = new Paragraph("Recipient Address:\n" + recipientAddress)
                .setFont(calibriFont)
                .setFontSize(11);
        document.add(recipient);

        // Spacing
        document.add(new Paragraph("\n"));

        // Add "Date of Report Generation: " followed by the current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateOfReport = new Paragraph("Date of Report Generation: " + now.format(formatter))
                .setFont(calibriFont)
                .setFontSize(11)
                .setBold();
        document.add(dateOfReport);

        // Add spacing after recipient address
        document.add(new Paragraph("\n"));

        // Add "Report on the results of the serological analysis" in bold
        Paragraph reportTitle = new Paragraph("Report on the results of the serological analysis")
                .setFont(calibriBoldFont)
                .setFontSize(11)
                .setBold();
        document.add(reportTitle);

        // Add spacing after report title
        document.add(new Paragraph("\n"));

        // Add "Analytical methods: " followed by AnalysisTypeName for selected checkboxes
        StringBuilder methods = new StringBuilder("Analytical methods: ");
        List<AnalysisType> selectedAnalysisTypes = new ArrayList<>();
        for (AnalysisType analysisType : study.getAnalysisTypes()) {
            if (analysisCheckboxMap.getOrDefault(analysisType.getId(), false)) {
                selectedAnalysisTypes.add(analysisType);
                methods.append(analysisType.getAnalysisName()).append(", ");
            }
        }
        if (methods.length() > 0) {
            methods.setLength(methods.length() - 2); // Remove the last comma and space
        }
        Paragraph analyticalMethods = new Paragraph(methods.toString())
                .setFont(calibriFont)
                .setFontSize(11);
        document.add(analyticalMethods);

        // Add spacing after analytical methods
        document.add(new Paragraph("\n"));

        // Add "Study: " followed by StudyName and studyDate
        Paragraph studyDetails = new Paragraph("Study: " + study.getStudyName() + ", " + study.getStudyDate().toString())
                .setFont(calibriFont)
                .setFontSize(11);
        document.add(studyDetails);


        // Add fields for details to the study (handwritten by user)
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Methodenvalidierung: ").setBold());
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Qualitätskontrolle: ").setBold());
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Bemerkungen: ").setBold());
        document.add(new Paragraph("\n"));


        // Add spacing after study details
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("The results can be found on the following page(s).").setBold());
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("The test report may not be reproduced without the written consent of the laboratory.").setBold());

        // Add a page break here to start the table on a new page
        document.add(new AreaBreak());


        // Create the table with appropriate number of columns, including the numbering column
        float[] tableColumnWidths = new float[selectedAnalysisTypes.size() + 2]; // +2 for numbering and sample ID
        tableColumnWidths[0] = 1; // For numbering column
        tableColumnWidths[1] = 2; // For Sample ID column, make it wider
        for (int i = 2; i < tableColumnWidths.length; i++) {
            tableColumnWidths[i] = 1; // Make each analysis column narrower
        }
        Table table = new Table(tableColumnWidths);
        table.setWidth(UnitValue.createPercentValue(100)); // Set the table width to 100% of the page

        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("No.")).setFont(calibriFont).setFontSize(11));
        table.addHeaderCell(new Cell().add(new Paragraph("Sample ID")).setFont(calibriFont).setFontSize(11));
        for (AnalysisType analysisType : selectedAnalysisTypes) {
            table.addHeaderCell(new Cell().add(new Paragraph(analysisType.getAnalysisName())).setFont(calibriFont).setFontSize(11));
        }

        // Add sample data to the table
        List<Sample> samples = study.getListOfSamples();
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))).setFont(calibriFont).setFontSize(11)); // Add numbering
            table.addCell(new Cell().add(new Paragraph(sample.getSample_barcode())).setFont(calibriFont).setFontSize(11));
            for (AnalysisType analysisType : selectedAnalysisTypes) {
                String result = GENERAL_UTIL.getAnalysisForSample(sample, analysisType.getId());
                table.addCell(new Cell().add(new Paragraph(result != null ? result : "")));
            }
        }

        // Add the table normally to follow the flow of the document
        document.add(new Paragraph("Results: ").setBold());
        document.add(table);
        document.add(new Paragraph("\n"));

        // Add "Textbaustein " followed by analysisName and description
        for (AnalysisType analysisType : selectedAnalysisTypes) {
            Paragraph textbaustein = new Paragraph("Textbaustein " + analysisType.getAnalysisName() + ": " + analysisType.getAnalysisDescription())
                    .setFont(calibriFont)
                    .setFontSize(11);
            document.add(textbaustein);
            document.add(new Paragraph("\n")); // Add line break after each analysis type
        }


        // Add extra spacing before the final section
        document.add(new Paragraph("\n"));

        // Add the final section with "Technical validation" and "Final validation" in a table
        float[] validationColumnWidths = {1, 1};
        Table validationTable = new Table(validationColumnWidths);
        validationTable.setWidth(UnitValue.createPercentValue(100));
        validationTable.setBorder(null);

        // Add validation headers
        validationTable.addCell(new Cell().add(new Paragraph("Technical validation" + "\n\n\n")
                        .setFont(calibriBoldFont)
                        .setFontSize(11)
                        .setBold())
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT));

        validationTable.addCell(new Cell().add(new Paragraph("Final validation" + "\n\n\n")
                        .setFont(calibriBoldFont)
                        .setFontSize(11)
                        .setBold())
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT));

        // Add signature lines
        validationTable.addCell(new Cell().add(new Paragraph("Datum, Unterschrift")
                        .setFont(calibriFont)
                        .setFontSize(11))
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT));

        validationTable.addCell(new Cell().add(new Paragraph("Datum, Unterschrift")
                        .setFont(calibriFont)
                        .setFontSize(11))
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(validationTable);

        document.close();
        pdfDoc.close();
    }

}


