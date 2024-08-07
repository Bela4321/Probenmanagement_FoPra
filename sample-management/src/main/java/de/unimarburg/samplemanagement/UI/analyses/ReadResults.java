package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import de.unimarburg.samplemanagement.utils.uploader.DownloadLinkAreaForArbeitsliste;
import de.unimarburg.samplemanagement.utils.uploader.ExcelUploader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@Route("/ReadResults")
public class ReadResults extends HorizontalLayout {

        private Study study;
        ExcelParser excelParser;
        private final SampleRepository sampleRepository;
        private ClientStateService clientStateService;

        @Autowired
        public ReadResults(ClientStateService clientStateService, SampleRepository sampleRepository, ExcelParser excelParser) {
            this.excelParser = excelParser;
            this.clientStateService = clientStateService;
            this.sampleRepository = sampleRepository;
            add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
            study = clientStateService.getClientState().getSelectedStudy();
            if (clientStateService.getClientState().getSelectedStudy() == null) {
                add("Please select a Study");
                return;
            }
            add(loadContent());
        }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        File uploadFolder = getUploadFolder();
        ExcelUploader uploadArea = new ExcelUploader(uploadFolder);
        DownloadLinkAreaForArbeitsliste linksArea = new DownloadLinkAreaForArbeitsliste(uploadFolder, excelParser);
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
