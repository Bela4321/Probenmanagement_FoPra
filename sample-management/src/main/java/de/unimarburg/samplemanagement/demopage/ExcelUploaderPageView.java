package de.unimarburg.samplemanagement.demopage;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.io.File;

@Route("upload-excel")
public class ExcelUploaderPageView extends VerticalLayout {

    public ExcelUploaderPageView() {
        File uploadFolder = getUploadFolder();
        ExcelUploader uploadArea = new ExcelUploader(uploadFolder);
        DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder);
        uploadArea.getUploadField().addSucceededListener(e -> {
            uploadArea.hideErrorField();
            linksArea.refreshFileLinks();
        });
        add(uploadArea, linksArea);
        // Add a link to the main view
        add(new RouterLink("Back to the main view", Demopage.class));
    }

    private static File getUploadFolder() {
        File folder = new File("uploaded-files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}