package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


@Route("/")
public class Main extends VerticalLayout {
    public Main() {
        Button button = new Button("Read Probe_ID");
        button.addClickListener(e-> UI.getCurrent().navigate("/Read_Probe_ID"));

        Button button4 = new Button("Studie_Anlegen");
        button4.addClickListener(e-> UI.getCurrent().navigate("/Studie_Anlegen"));
        Button button5 = new Button("Einsender_Anlegen");
        button5.addClickListener(e-> UI.getCurrent().navigate("/Einsender_Anlegen"));
        Button button6 = new Button("Probe_Zuordnen");
        button6.addClickListener(e-> UI.getCurrent().navigate("/Probe_Zuordnen"));
        Button button7 = new Button("Befund_erstellen");
        button7.addClickListener(e-> UI.getCurrent().navigate("/Befund_erstellen"));
        Button button10 = new Button("Search_with_Barcode_ID");
        button10.addClickListener(e-> UI.getCurrent().navigate("/Search_with_Barcode_ID"));
        Button button11 = new Button("Excel_Vorlage");
        button11.addClickListener(e-> UI.getCurrent().navigate("/Excel_Vorlage"));
        Button button12 = new Button("Check_Barcode_Numbers");
        button12.addClickListener(e-> UI.getCurrent().navigate("/Check_Barcode_Numbers"));
        add(button,button4,button5,button6,button7,button10,button11,button12);
        setVisible(true);
    }
}
