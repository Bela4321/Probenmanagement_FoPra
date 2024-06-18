package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.UI.Main;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import de.unimarburg.samplemanagement.utils.uploader.DownloadLinksArea;
import de.unimarburg.samplemanagement.utils.uploader.ExcelUploader;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.router.RouterLink;

import java.io.File;


@Route("/SampleDelivery")
public class SampleDelivery extends VerticalLayout {

    @Autowired
    public SampleDelivery(ClientStateService clientStateService) {
        File uploadFolder = getUploadFolder();
        ExcelUploader uploadArea = new ExcelUploader(uploadFolder);
        DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder);
        uploadArea.getUploadField().addSucceededListener(e -> {
            uploadArea.hideErrorField();
            linksArea.refreshFileLinks();
        });
        add(uploadArea, linksArea);
        //Add a button to call the readExcelFile on the uploadFolder
        // Add a link to the main view
        add(new RouterLink("Back to the main view", Main.class));
    }
    private static File getUploadFolder() {
        File folder = new File("uploaded-files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }


}

