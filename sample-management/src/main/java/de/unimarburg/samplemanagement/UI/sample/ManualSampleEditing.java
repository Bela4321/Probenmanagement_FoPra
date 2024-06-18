package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/ManualSampleEditing")
public class ManualSampleEditing extends HorizontalLayout {

    @Autowired
    public ManualSampleEditing(ClientStateService clientStateService) {
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getUserState().getSelectedStudy()));
        if (clientStateService.getUserState().getSelectedStudy() == null) {
            add("Please select a study");
            return;
        }
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add("Manual Sample Editing will be available here.");//todo implement manual sample editing


        add(verticalLayout);
    }
}
