package de.unimarburg.samplemanagement.UI;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Component;


@Component
public class SidebarFactory extends VerticalLayout {


    public VerticalLayout getSidebar() {
        VerticalLayout sidebar = new VerticalLayout();
        Button button1 = new Button("Home");
        button1.addClickListener(e-> UI.getCurrent().navigate("/"));
        Button button2 = new Button("Studies");
        button2.addClickListener(e-> UI.getCurrent().navigate("/Studies"));
        sidebar.add(button1, button2);
        return sidebar;
    }
}
