package de.unimarburg.samplemanagement.UI;

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
        Button button = new Button("Read Probe_ID");
        button.addClickListener(e-> UI.getCurrent().navigate("/Read_Probe_ID"));

        Button button4 = new Button("Create_Study");
        button4.addClickListener(e-> UI.getCurrent().navigate("/create_Study"));
        Button button5 = new Button("Einsender_Anlegen");
        button5.addClickListener(e-> UI.getCurrent().navigate("/Einsender_Anlegen"));
        Button button6 = new Button("Probe_Zuordnen");
        button6.addClickListener(e-> UI.getCurrent().navigate("/Probe_Zuordnen"));
        Button button7 = new Button("Befund_erstellen");
        button7.addClickListener(e-> UI.getCurrent().navigate("/CreateReport"));
        Button button10 = new Button("Manage_Subjects");
        button10.addClickListener(e-> UI.getCurrent().navigate("/Manage_Subjects"));
        Button button11 = new Button("Excel_Vorlage");
        button11.addClickListener(e-> UI.getCurrent().navigate("/Excel_Vorlage"));
        Button button12 = new Button("Manage_Samples");
        button12.addClickListener(e-> UI.getCurrent().navigate("/Manage_Samples"));
        Button button13 = new Button("Analyseergebnisse eintragen");
        button13.addClickListener(e-> UI.getCurrent().navigate("/analysis_result"));

        verticalLayout.add(button, button4, button5, button6, button7, button10, button11, button12, button13);
        add(verticalLayout);
    }
}
