package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import de.unimarburg.samplemanagement.utils.uploader.DownloadLinksArea;
import de.unimarburg.samplemanagement.utils.uploader.ExcelUploader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;


@Route("/SampleDelivery")
public class SampleDelivery extends HorizontalLayout {
    ExcelParser excelParser;

    @Autowired
    public SampleDelivery(ClientStateService clientStateService, ExcelParser excelParser) {
        this.excelParser = excelParser;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (clientStateService.getClientState().getSelectedStudy() == null) {
            add("Please select a study first");
            return;
        }
        // Set the content
        add(getContent());
    }

    private VerticalLayout getContent() {
        VerticalLayout body = new VerticalLayout();
        File uploadFolder = getUploadFolder();
        ExcelUploader uploadArea = new ExcelUploader(uploadFolder);
        DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder, excelParser);
        uploadArea.getUploadField().addSucceededListener(e -> {
            uploadArea.hideErrorField();
            linksArea.refreshFileLinks();
        });
        body.add(uploadArea, linksArea);
        return body;
    }
    private static File getUploadFolder() {
        File folder = new File("uploaded-files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}

