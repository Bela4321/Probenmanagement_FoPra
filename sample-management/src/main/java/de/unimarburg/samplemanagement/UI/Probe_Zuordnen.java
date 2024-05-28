package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/Probe_Zuordnen")
public class Probe_Zuordnen extends VerticalLayout {
    public Probe_Zuordnen() {
        add("Studien und Einsender in der Software mit den relevanten Daten „anlegen“ und Proben zuordnen \n");
        add("Proben ID einen oder mehrere Parameter (Analysen) zuzuweisen");
        setVisible(true);
    }
}
