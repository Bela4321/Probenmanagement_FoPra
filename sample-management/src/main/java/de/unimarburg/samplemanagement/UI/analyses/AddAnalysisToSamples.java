package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/AddAnalysisToSample")
public class AddAnalysisToSamples extends HorizontalLayout {
    private final SampleRepository sampleRepository;
    ClientStateService clientStateService;
    Study study;

    @Autowired
    public AddAnalysisToSamples(ClientStateService clientStateService, SampleRepository sampleRepository) {
        this.clientStateService = clientStateService;
        this.sampleRepository = sampleRepository;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        study = clientStateService.getClientState().getSelectedStudy();
        if (clientStateService.getClientState().getSelectedStudy() == null) {
            add("Bitte eine Studie auswählen");
            return;
        }
        add(loadContent());
    }

    private VerticalLayout loadContent() {
        VerticalLayout body = new VerticalLayout();
        Grid<Sample> sampleGrid = new Grid<>();
        sampleGrid.setItems(study.getListOfSamples());

        //sample info
        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");
        sampleGrid.addColumn(Sample::getSample_type).setHeader("Sample Type");
        sampleGrid.addColumn(Sample::getSample_amount).setHeader("Sample Amount");

        //add checkbox for each analysis type
        for (AnalysisType analysisType :study.getAnalysisTypes()) {
            sampleGrid.addComponentColumn(sample -> {
                Button button = new Button("Add");
                button.addClickListener(e -> {
                    sample.getListOfAnalysis().add(new Analysis(analysisType, sample));
                    sampleRepository.save(sample);
                });
                if (sample.getListOfAnalysis().stream().anyMatch(a -> a.getAnalysisType().getId().equals(analysisType.getId()))) {
                    button.setEnabled(false);
                } else {
                    button.setDisableOnClick(true);
                }
                return button;
            }).setHeader(analysisType.getAnalysisName());
        }
        body.add(sampleGrid);

        return body;
    }
}
