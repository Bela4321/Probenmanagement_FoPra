package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.AnalysisType;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.repository.SubjectRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/viewSingleSample")
public class DetailedSingleSampleView extends HorizontalLayout {

    SampleRepository sampleRepository;
    ClientStateService clientStateService;
    @Autowired
    public DetailedSingleSampleView(SampleRepository sampleRepository, ClientStateService clientStateService) {
        this.sampleRepository = sampleRepository;
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (clientStateService.getClientState().getSelectedSample() == null){
            add("No sample selected");
            return;
        }
        VerticalLayout content = getContent();
        add(content);

    }

    private VerticalLayout getContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        //detailed view of one sample
        Sample selectedSample = clientStateService.getClientState().getSelectedSample();
        Grid<Sample> grid = new Grid();
        //add columns
        grid.addColumn(Sample::getStudyName).setHeader("Study Name");
        grid.addColumn(Sample::getSubjectAlias).setHeader("Subject Alias");
        grid.addColumn(Sample::getSample_barcode).setHeader("Barcode");
        grid.addColumn(Sample::getSampleDate).setHeader("Sample Date");
        grid.addColumn(sample -> sample.getSampleDelivery().getRunningNumber()).setHeader("Sample Delivery Running Number");
        grid.addColumn(Sample::getSample_type).setHeader("Sample Type");
        grid.addColumn(Sample::getSample_amount).setHeader("Sample Amount");
        grid.addColumn(Sample::getCoordinates).setHeader("Coordinates");
        for (int i = 0; i<selectedSample.getListOfAnalysis().size(); i++){
            AnalysisType analysisType = selectedSample.getListOfAnalysis().get(i).getAnalysisType();
            int finalI = i;
            grid.addColumn(s->s.getListOfAnalysis().get(finalI).getAnalysisResult()).setHeader(analysisType.getAnalysisName());
        }

        grid.setItems(selectedSample);
        verticalLayout.add(grid);
        return verticalLayout;
    }
}
