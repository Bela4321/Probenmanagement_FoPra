package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/Search_with_Barcode_ID")
public class Search_with_Barcode_ID extends VerticalLayout {
    public Search_with_Barcode_ID() {
        add("Suchfunktion nach der Barcode-ID, die auf einen „Klick“  die Information liefert : \n" +
                "Proband 12345 , Studienname XYZ , Ergebnis 1 , Ergebnis 2, Ergebnis 3 …");
        setVisible(true);
    }
}
