package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


@Route("/Excel_Vorlage")
public class Excel_Vorlage extends VerticalLayout {
    public Excel_Vorlage() {
        add("Der Einsender bekommt von uns eine EXCEL Vorlage zugesendet, die er zu jeder Probenlieferung beifügt und mit der sowohl die Proben ID als auch der/die zu bestimmenden Parameter angefordert werden.\n" +
                "Die Vorlage und was auszufüllen ist können wir bestimmen !\n" +
                "d.h. hier könnte z.B. ein Template als „Anforderungsdokument“ erstellt werden welches in die  Probenerfassung eingebunden werden könnte ?!");
        setVisible(true);
    }
}
