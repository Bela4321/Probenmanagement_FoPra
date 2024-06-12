package de.unimarburg.samplemanagement.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.unimarburg.samplemanagement.model.Study;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SIDEBAR_FACTORY extends VerticalLayout {

    public static VerticalLayout getSidebar(Study study) {
        VerticalLayout sidebar = new VerticalLayout();
        Button button1 = new Button("Home");
        button1.addClickListener(e-> UI.getCurrent().navigate("/"));
        Button button2 = new Button("Studies");
        button2.addClickListener(e-> UI.getCurrent().navigate("/Studies"));
        sidebar.add(button1, button2);
        if (study == null) {
            return sidebar;
        }
        sidebar.add(new Text("-----------------"));
        sidebar.add(new Text("Study: " + study.getStudyName()));
        sidebar.add(new Text("-----------------"));

        Map<String,String> studyActions = ACTION_LISTS.getStudySpecificActions();
        for (Map.Entry<String, String> entry : studyActions.entrySet()) {
            Button button = new Button(entry.getKey());
            button.addClickListener(e-> UI.getCurrent().navigate(entry.getValue()));
            sidebar.add(button);
        }


        return sidebar;
    }
}
