package de.unimarburg.samplemanagement.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import de.unimarburg.samplemanagement.UI.Main;
import de.unimarburg.samplemanagement.UI.sample.SampleDelivery;
import de.unimarburg.samplemanagement.UI.study.StudiesView;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.utils.uploader.DownloadLinksArea;
import de.unimarburg.samplemanagement.utils.uploader.ExcelUploader;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.springframework.stereotype.Component;
import oshi.util.tuples.Pair;

import java.util.List;


@Component
public class SIDEBAR_FACTORY extends VerticalLayout {

//    public static VerticalLayout getSidebar(Study study) {
//        VerticalLayout sidebar = new VerticalLayout();
//        Button button1 = new Button("Home");
//        button1.addClickListener(e-> UI.getCurrent().navigate("/"));
//        Button button2 = new Button("Studies");
//        button2.addClickListener(e-> UI.getCurrent().navigate("/Studies"));
//        sidebar.add(button1, button2);
//        if (study == null) {
//            return sidebar;
//        }
//        sidebar.add(new Text("--------------"));
//        sidebar.add(new Text("Study: " + study.getStudyName()));
//        sidebar.add(new Text("--------------"));
//
//        List<Pair<String, String>> studyActions = ACTION_LISTS.getStudySpecificActions();
//        for (Pair<String, String> entry : studyActions) {
//            Button button = new Button(entry.getA());
//            button.addClickListener(e-> UI.getCurrent().navigate(entry.getB()));
//            sidebar.add(button);
//        }
//        // set width of sidebar to max 25% of the screen
//        sidebar.getStyle().set("width", "30%");
//        return sidebar;
//    }

    public static SideNav getSidebar(Study study) {
        SideNav genNav = new SideNav();
        genNav.setLabel("General");
        SideNavItem home = new SideNavItem("Home", Main.class, VaadinIcon.HOME.create());
        SideNavItem studies = new SideNavItem("Studies", StudiesView.class, VaadinIcon.BOOK.create());
        genNav.addItem(home, studies);
        if (study == null) {
            return genNav;
        }
        SideNav studyNav = new SideNav();
        studyNav.setLabel("Study: " + study.getStudyName());
        studyNav.addItem(home, studies);
        List<Pair<String, Class>> studyActions = ACTION_LISTS.getStudySpecificActions();
        for (Pair<String, Class> entry : studyActions) {
            SideNavItem button = new SideNavItem(entry.getA(), entry.getB(), VaadinIcon.ARROW_RIGHT.create());
            studyNav.addItem(button);

        }

        return studyNav;
    }
}
