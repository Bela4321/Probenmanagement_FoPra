package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("/ViewSampleAnalysis")
public class AnalysisResultView extends HorizontalLayout {
    private ClientStateService clientStateService;

    @Autowired
    public AnalysisResultView(ClientStateService clientStateService) {
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));


        add(loadData());
    }

    private VerticalLayout loadData() {
        VerticalLayout body = new VerticalLayout();

        Study study = clientStateService.getClientState().getSelectedStudy();

        if (study == null) {
            add("Please select a Study");
            return body;
        }

        List<Sample> samples = study.getListOfSamples();
        Grid<Sample> sampleGrid = new Grid<>();
        sampleGrid.setItems(samples);
        List<AnalysisType> uniqueAnalysisTypes = study.getAnalysisTypes();
        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");

        for (AnalysisType analysisType : uniqueAnalysisTypes) {
            sampleGrid.addColumn(sample -> GENERAL_UTIL.getAnalysisForSample(sample, analysisType.getId()))
                    .setHeader(analysisType.getAnalysisName());
        }
        body.add(sampleGrid);

        return body;
    }
}

