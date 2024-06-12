package de.unimarburg.samplemanagement.UI.sampleDelivery;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.service.ClientStateService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/SampleDelivery")
public class SampleDeliveryByFile extends HorizontalLayout {

    @Autowired
    public SampleDeliveryByFile(ClientStateService clientStateService) {
        add("Samples über Datei einlesen");
        setVisible(true);
    }
}
