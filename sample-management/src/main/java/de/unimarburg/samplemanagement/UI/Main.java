package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;


@Route("/")
public class Main extends HorizontalLayout {

    @Autowired
    public Main(ClientStateService clientStateService) {
        clientStateService.setUserState(null);
        add(SIDEBAR_FACTORY.getSidebar(null));
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Text("Welcome to the Sample Management System\nSelect an option from the sidebar to get started :)"));
        Button impressumButton = new Button("Impressum");
        impressumButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("impressum"));
        });
        Button datenschutzerklaerungButton = new Button("Datenschutzerklärung");
        datenschutzerklaerungButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("datenschutzerklaerung"));
        });
        add(verticalLayout);
    }
}
