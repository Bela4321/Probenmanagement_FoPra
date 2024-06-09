package de.unimarburg.samplemanagement.UI.inputAnalysisResult;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/input_analysis")
public class InputAnalysisResultNoParam extends VerticalLayout {
    public InputAnalysisResultNoParam() {
        add(new Span("Bitte eine Studie auswählen"));
    }
}
