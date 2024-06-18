package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;


@Route("/")
public class Main extends HorizontalLayout {
    public Main() {
        add(SIDEBAR_FACTORY.getSidebar(null));
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Text("Welcome to the Sample Management System\n"));
        verticalLayout.add(new Text("Select an option from the sidebar to get started :)"));
        add(verticalLayout);
    }
}
