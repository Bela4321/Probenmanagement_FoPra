package de.unimarburg.samplemanagement.utils;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import de.unimarburg.samplemanagement.UI.Main;
import de.unimarburg.samplemanagement.UI.general_info.EditAddresses;
import de.unimarburg.samplemanagement.UI.sample.SampleView;
import de.unimarburg.samplemanagement.UI.study.StudiesView;
import de.unimarburg.samplemanagement.model.Study;
import org.springframework.stereotype.Component;
import oshi.util.tuples.Pair;

import java.util.List;


@Component
public class SIDEBAR_FACTORY extends VerticalLayout {

    public static SideNav getSidebar(Study study) {
        SideNav genNav = new SideNav();
        genNav.setLabel("General");
        SideNavItem home = new SideNavItem("Home", Main.class, VaadinIcon.HOME.create());
        SideNavItem studies = new SideNavItem("Studies", StudiesView.class, VaadinIcon.BOOK.create());
        SideNavItem samples = new SideNavItem("Samples", SampleView.class, VaadinIcon.BARCODE.create());
        SideNavItem editAddress = new SideNavItem("Change Address", EditAddresses.class, VaadinIcon.MAILBOX.create());
        genNav.addItem(home, studies, samples, editAddress);
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
