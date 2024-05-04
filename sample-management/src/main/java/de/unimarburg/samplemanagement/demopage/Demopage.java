package de.unimarburg.samplemanagement.demopage;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class Demopage extends VerticalLayout {

        public Demopage() {
            add("Hello, World!");
        }
}
