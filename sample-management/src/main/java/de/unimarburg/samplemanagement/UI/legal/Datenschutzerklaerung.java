package de.unimarburg.samplemanagement.UI.legal;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;

@Route("/datenschutzerklaerung")
public class Datenschutzerklaerung extends HorizontalLayout {

    public Datenschutzerklaerung() {
        add(SIDEBAR_FACTORY.getSidebar(null));
        String sourcefile = "legal/dataprotection.md";
        String contentMD = GENERAL_UTIL.readFileToString(sourcefile);
        String contentHTML = GENERAL_UTIL.markdownToHtml(contentMD);
        Html html = new Html("<div>"+contentHTML+"</div>");
        Div contentDiv = new Div(html);
        add(contentDiv);
    }
}
