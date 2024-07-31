package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Sample;

import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.service.SampleService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;


@Route("/samples")
public class SampleView extends HorizontalLayout {

    private final SampleService sampleService;
    private final Grid<Sample> grid = new Grid<>(Sample.class);

    private final TextField barcodeFilter = new TextField();
    private final TextField studyNameFilter = new TextField();
    private final TextField subjectAliasFilter = new TextField();
    private final ClientStateService clientStateService;

    @Autowired
    public SampleView(SampleService sampleService, ClientStateService clientStateService) {
        this.sampleService = sampleService;
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        VerticalLayout content = getContent();
        add(content);
    }

    private VerticalLayout getContent() {
        VerticalLayout content = new VerticalLayout();
        content.add(addTitle());
        content.add(addFilters());
        content.add(createGrid());
        updateGrid();
        return content;
    }

    private Component addTitle() {
        H1 title = new H1("Probencheckliste");
        return title;
    }

    private Component addFilters() {
        barcodeFilter.setPlaceholder("Filter by Barcode...");
        studyNameFilter.setPlaceholder("Filter by Study Name...");
        subjectAliasFilter.setPlaceholder("Filter by Subject Alias...");

        barcodeFilter.addValueChangeListener(e -> updateGrid());
        studyNameFilter.addValueChangeListener(e -> updateGrid());
        subjectAliasFilter.addValueChangeListener(e -> updateGrid());

        HorizontalLayout filters = new HorizontalLayout(barcodeFilter, studyNameFilter, subjectAliasFilter);
        return filters;
    }

    private Component createGrid() {
        grid.removeAllColumns(); // Remove default columns

        grid.addColumn(Sample::getStudyName).setHeader("Study Name").isSortable();
        grid.addColumn(sample -> sample.getSubjectAlias() != null ? sample.getSubjectAlias().toString() : "").setHeader("Subject Alias").isSortable();
        grid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode").isSortable();
        grid.addColumn(Sample::getCoordinates).setHeader("Coordinates").isSortable();
        grid.addColumn(Sample::getSample_type).setHeader("Sample Type").isSortable();

        // see metadata about analysis
        grid.addColumn(Sample::getNumberAnalyses).setHeader("Number of Analyses").isSortable();
        grid.addColumn(Sample::getNumberFinishedAnalyses).setHeader("Number of Finished Analyses").isSortable();

        //on selection, jump to detailed showcase
        grid.addAttachListener(e -> {
            grid.addItemDoubleClickListener(event -> {
                clientStateService.getClientState().setSelectedSample(event.getItem());
                grid.getUI().ifPresent(ui -> ui.navigate("viewSingleSample"));
            });
        });
        return grid;
    }

    private void updateGrid() {
        List<Sample> samples = sampleService.findAll().stream()
                .filter(sample -> {
                    boolean matchesBarcode = sample.getSample_barcode().toLowerCase().contains(barcodeFilter.getValue().toLowerCase());
                    boolean matchesStudyName = sample.getStudyName().toLowerCase().contains(studyNameFilter.getValue().toLowerCase());
                    boolean matchesSubjectAlias = true;
                    if (!subjectAliasFilter.getValue().isEmpty()) {
                        try {
                            Long aliasValue = Long.parseLong(subjectAliasFilter.getValue());
                            matchesSubjectAlias = aliasValue.equals(sample.getSubjectAlias());
                        } catch (NumberFormatException e) {
                            matchesSubjectAlias = false;
                        }
                    }
                    return matchesBarcode && matchesStudyName && matchesSubjectAlias;
                })
                .collect(Collectors.toList());

        grid.setItems(samples);
    }
}
