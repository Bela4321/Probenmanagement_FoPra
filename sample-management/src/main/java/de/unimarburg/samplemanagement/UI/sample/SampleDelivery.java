package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/SampleDelivery")
public class SampleDelivery extends HorizontalLayout {

    @Autowired
    public SampleDelivery(ClientStateService clientStateService) {
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));
        if (clientStateService.getUserState().getSelectedStudy() == null) {
            add("Please select a study");
            return;
        }
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add("Samples can be delivered here.");//todo implement sample delivery

        add(verticalLayout);
    }
}
