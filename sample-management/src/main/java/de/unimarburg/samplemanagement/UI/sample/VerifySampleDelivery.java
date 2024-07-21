package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.SampleDelivery;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@Route("/VerifySampleDelivery")
public class VerifySampleDelivery extends HorizontalLayout {

    ClientStateService clientStateService;
    Study study;
    SampleDelivery sampleDelivery;
    Grid<Sample> sampleGrid;
    List<Sample> sampleList;

    @Autowired
    public VerifySampleDelivery(ClientStateService clientStateService) {
        this.clientStateService = clientStateService;
        this.study = clientStateService.getClientState().getSelectedStudy();
        this.sampleDelivery = clientStateService.getClientState().getSelectedSampleDelivery();
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (study == null) {
            add("No study selected");
            return;
        }

        if (sampleDelivery == null) {
            //select most recent sample delivery
            sampleDelivery = study.getSampleDeliveryList().get(study.getSampleDeliveryList().size() - 1);
        }

        VerticalLayout content = getContent();
        add(content);
    }


    private void initGrid() {
        this.sampleGrid = new Grid<>();
        this.sampleList = sampleDelivery.getSamples();
        sampleList.sort(Comparator.comparing(Sample::getCoordinates));
        sampleGrid.setItems(sampleList);
        //highlight first sample
        sampleGrid.select(sampleList.get(0));
        sampleGrid.addColumn(Sample::getCoordinates).setHeader("Coordinates");
        sampleGrid.addColumn(Sample::getSample_barcode).setHeader("Barcode");
        sampleGrid.addColumn(Sample::getSample_type).setHeader("Type");
    }

    private void changeSampleDeliveryofGrid() {
        this.sampleList = sampleDelivery.getSamples();
        sampleList.sort(Comparator.comparing(Sample::getCoordinates));
        sampleGrid.setItems(sampleList);
        //highlight first sample
        sampleGrid.select(sampleList.get(0));
        sampleGrid.getDataProvider().refreshAll();
    }

    private VerticalLayout getContent() {
        VerticalLayout content = new VerticalLayout();

        //sampleDeivery selection
        content.add(new Text("Verify Sample Delivery:"));
        Select<SampleDelivery> deliveryFilter = new Select<>();
        deliveryFilter.setLabel("Select Delivery to verify");
        deliveryFilter.setItems(study.getSampleDeliveryList());
        deliveryFilter.setEmptySelectionAllowed(true);
        deliveryFilter.setRenderer(new TextRenderer<>(sampleDelivery -> String.valueOf(sampleDelivery.getRunningNumber())));
        //set starting value
        deliveryFilter.setValue(sampleDelivery);
        deliveryFilter.addValueChangeListener(e -> {
            sampleDelivery = e.getValue();
            clientStateService.getClientState().setSelectedSampleDelivery(sampleDelivery);
            changeSampleDeliveryofGrid();
        });

        initGrid();
        //verification textfield
        TextField verificationField = new TextField();
        verificationField.setLabel("Verification");
        verificationField.setPlaceholder("Enter verification here");
        //add action listener for enter
        verificationField.addKeyPressListener(Key.ENTER, e -> {
            //is content same as barcode?
            String verification = verificationField.getValue();
            String barcode = sampleGrid.getSelectedItems().iterator().next().getSample_barcode();
            if (verification.equals(barcode)) {
                verificationSucess();
                verificationField.clear();
            } else {
                //verification failed
                Notification.show("Verification failed, mismatch between barcode and verification");
            }
        });

        content.add(deliveryFilter,verificationField,sampleGrid);
        return content;
    }

    private void verificationSucess() {
        //remove sample from list
        sampleList.remove(sampleGrid.getSelectedItems().iterator().next());
        //remove sample from grid
        sampleGrid.setItems(sampleList);
        //highlight next sample
        sampleGrid.select(sampleList.get(0));
        //show notification
        Notification.show("Verification successful");
        //update grid
        sampleGrid.getDataProvider().refreshAll();

        //is last removed sample?
        if (sampleList.isEmpty()) {
            Notification.show("All samples verified");
        }
    }
}
