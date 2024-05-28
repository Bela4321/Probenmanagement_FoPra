package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/Befund_erstellen")
public class Befund_erstellen extends VerticalLayout {
    public Befund_erstellen() {
        add("Nach der Analyse einen „Befund“  (Sammel- oder Einzelbefund) erstellen");
        Button button8 = new Button("Einzel_Befund_erstellen");
        Button button9 = new Button("Sammel_Befund_erstellen");
        add(button8,button9);
        setVisible(true);
    }
}
