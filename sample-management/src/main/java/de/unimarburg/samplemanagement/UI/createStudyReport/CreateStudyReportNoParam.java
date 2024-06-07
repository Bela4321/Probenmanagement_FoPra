package de.unimarburg.samplemanagement.UI.createStudyReport;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("create_study_report")
public class CreateStudyReportNoParam extends VerticalLayout {
    public CreateStudyReportNoParam() {
        add(new Span("Bitte eine Studie auswählen"));
    }
}