package de.unimarburg.samplemanagement.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DISPLAY_UTILS {

    private static final Double DEFAULT_RATIO = 16./9;

    public static Component geBoxAlignment(Component... components) {
        return geBoxAlignment(DEFAULT_RATIO, components);
    }

    public static Component geBoxAlignment(Double ratio, Component... components) {
        int items = components.length;
        int height = (int) Math.ceil(Math.sqrt(items/(1+ratio)));
        int width =  (int) Math.ceil(items /(double) height);

        VerticalLayout BoxAlignment = new VerticalLayout();
        for (int row=0; row<height; row++) {
            HorizontalLayout rowLayout = new HorizontalLayout();
            for (int col=0; col<width; col++) {
                int index = row*width + col;
                if (index < items) {
                    rowLayout.add(components[index]);
                }
            }
            rowLayout.getThemeList().add("spacing");
            rowLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
            BoxAlignment.add(rowLayout);
        }
        return BoxAlignment;
    }
}
