package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/Einsender_Anlegen")
public class Einsender_Anlegen extends VerticalLayout {
    public Einsender_Anlegen() {
        add("Studien und Einsender in der Software mit den relevanten Daten „anlegen");
        setVisible(true);
    }
}
