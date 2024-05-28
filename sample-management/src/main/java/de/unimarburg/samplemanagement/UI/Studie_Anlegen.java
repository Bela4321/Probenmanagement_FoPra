package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/Studie_Anlegen")
public class Studie_Anlegen extends VerticalLayout {
    public Studie_Anlegen() {
        add("Studien und Einsender in der Software mit den relevanten Daten „anlegen");
        setVisible(true);
    }
}
