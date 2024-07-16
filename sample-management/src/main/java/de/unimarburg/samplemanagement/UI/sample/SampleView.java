package de.unimarburg.samplemanagement.UI.sample;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Sample;

import de.unimarburg.samplemanagement.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Route("/samples")
public class SampleView extends VerticalLayout {

    private final SampleService sampleService;
    private final Grid<Sample> grid = new Grid<>(Sample.class);

    private final TextField barcodeFilter = new TextField();
    private final TextField studyNameFilter = new TextField();
    private final TextField subjectAliasFilter = new TextField();

    @Autowired
    public SampleView(SampleService sampleService) {
        this.sampleService = sampleService;
        addTitle();
        addFilters();
        createGrid();
        updateGrid();
    }

    private void addTitle() {
        H1 title = new H1("Probencheckliste");
        add(title);
    }

    private void addFilters() {
        barcodeFilter.setPlaceholder("Filter by Barcode...");
        studyNameFilter.setPlaceholder("Filter by Study Name...");
        subjectAliasFilter.setPlaceholder("Filter by Subject Alias...");

        barcodeFilter.addValueChangeListener(e -> updateGrid());
        studyNameFilter.addValueChangeListener(e -> updateGrid());
        subjectAliasFilter.addValueChangeListener(e -> updateGrid());

        HorizontalLayout filters = new HorizontalLayout(barcodeFilter, studyNameFilter, subjectAliasFilter);
        add(filters);
    }

    private void createGrid() {
        grid.removeAllColumns(); // Remove default columns

        grid.addColumn(Sample::getStudyName).setHeader("Study Name");
        grid.addColumn(sample -> sample.getSubjectAlias() != null ? sample.getSubjectAlias().toString() : "").setHeader("Subject Alias");
        grid.addColumn(Sample::getSample_barcode).setHeader("Sample Barcode");
        grid.addColumn(Sample::getCoordinates).setHeader("Coordinates");
        grid.addColumn(Sample::getSample_type).setHeader("Sample Type");

        // see metadata about analysis
        grid.addColumn(Sample::getNumberAnalyses).setHeader("Number of Analyses");
        grid.addColumn(Sample::getNumberFinishedAnalyses).setHeader("Number of Finished Analyses");

        add(grid);
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
