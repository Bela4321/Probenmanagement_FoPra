package de.unimarburg.samplemanagement.UI.analyses;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.*;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.DISPLAY_UTILS;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

        //dropdown filter for deliveries
        HorizontalLayout filterLayout = new HorizontalLayout();
        Select<SampleDelivery> deliveryFilter = new Select<>();
        deliveryFilter.setLabel("Filter by Delivery");
        deliveryFilter.setItems(study.getSampleDeliveryList());
        deliveryFilter.setEmptySelectionAllowed(true);
        deliveryFilter.setRenderer(new TextRenderer<>(sampleDelivery -> String.valueOf(sampleDelivery.getRunningNumber())));
        deliveryFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                sampleGrid.setItems(e.getValue().getSamples());
            } else {
                sampleGrid.setItems(study.getListOfSamples());
            }
        });
        filterLayout.add(deliveryFilter);

        //add-all buttons
        List<Button> add_all_buttons = new ArrayList<>();
        for (AnalysisType analysisType :study.getAnalysisTypes()) {
            Button button = new Button("Add all " + analysisType.getAnalysisName());
            button.addClickListener(e -> {
                for (Sample sample : study.getListOfSamples()) {
                    if (deliveryFilter.getValue() == null || deliveryFilter.getValue().getSamples().contains(sample)) {
                        if (sample.getListOfAnalysis().stream().noneMatch(a -> a.getAnalysisType().getId().equals(analysisType.getId()))) {
                            sample.getListOfAnalysis().add(new Analysis(analysisType, sample));
                            sampleRepository.save(sample);
                        }
                    }
                }
                sampleGrid.getDataProvider().refreshAll();
            });
            add_all_buttons.add(button);
        }
        filterLayout.add(DISPLAY_UTILS.getBoxAlignment(add_all_buttons.toArray(new Button[0])));


        body.add(filterLayout);

        return body;
    }
}
