package de.unimarburg.samplemanagement.demopage;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/demopage")
public class Demopage2 extends VerticalLayout {

        public Demopage2() {
            add("Hello, World2!");
        }
}
