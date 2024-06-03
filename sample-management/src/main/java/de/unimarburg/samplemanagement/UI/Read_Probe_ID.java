package de.unimarburg.samplemanagement.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
@Route("/Read_Probe_ID")
public class Read_Probe_ID extends VerticalLayout{
    public Read_Probe_ID(){
        add("Eingehende Proben erfassen / verwalten (Einlesen der Proben-ID, mit Barcodescanner oder als Excel-Import)\n");
        Button button1 = new Button("Read Barcode");
        Button button2 = new Button("Import Excel");
        add(button1,button2);
        setVisible(true);
    }
}
