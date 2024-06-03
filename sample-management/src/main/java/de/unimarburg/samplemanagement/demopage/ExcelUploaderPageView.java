package de.unimarburg.samplemanagement.demopage;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.io.File;

@Route("upload-excel")
public class ExcelUploaderPageView extends VerticalLayout {

    public ExcelUploaderPageView() {
        File uploadFolder = getUploadFolder();
        Exceluploader uploadArea = new Exceluploader(uploadFolder);
        DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder);
        uploadArea.getUploadField().addSucceededListener(e -> {
            uploadArea.hideErrorField();
            linksArea.refreshFileLinks();
        });
        add(uploadArea, linksArea);
    }

    private static File getUploadFolder() {
        File folder = new File("uploaded-files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}