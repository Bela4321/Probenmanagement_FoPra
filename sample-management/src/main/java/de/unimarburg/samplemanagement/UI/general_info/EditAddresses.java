package de.unimarburg.samplemanagement.UI.general_info;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.ReportAuthor;
import de.unimarburg.samplemanagement.repository.AddressStoreRepository;
import de.unimarburg.samplemanagement.repository.ReportAuthorRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Route("/edit_addresses")
public class EditAddresses extends HorizontalLayout {


    private final AddressStoreRepository addressStoreRepository;
    private final ReportAuthorRepository reportAuthorRepository;

    @Autowired
    public EditAddresses(AddressStoreRepository addressStoreRepository, ClientStateService clientStateService, ReportAuthorRepository reportAuthorRepository) {
        this.addressStoreRepository = addressStoreRepository;
        this.reportAuthorRepository = reportAuthorRepository;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        add(loadContent());
    }

    private Component loadContent() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        // change address
        VerticalLayout addressEditor = new VerticalLayout();
        addressEditor.add(new Text("This is our current Address"));
        addressEditor.setWidthFull();
        TextArea addressField = new TextArea();
        addressField.setWidth("400px");
        String ownAddress = addressStoreRepository.getOwnAddress();

        addressField.setValue(ownAddress);
        Button saveButton = new Button("Save");
        saveButton.addClickListener(e -> {
            addressStoreRepository.setOwnAddress(addressField.getValue());
            Notification.show("Address saved");
        });
        addressEditor.add(addressField,saveButton);

        // add/remove report authors
        VerticalLayout reportAuthorsEditor = new VerticalLayout();
        List<ReportAuthor> authors = reportAuthorRepository.findAll();
        reportAuthorsEditor.add(new Text("Report Authors"));
        Grid<ReportAuthor> authorGrid = new Grid<>();
        authorGrid.setItems(authors);
        authorGrid.addColumn(ReportAuthor::getName).setHeader("Name");
        authorGrid.addColumn(ReportAuthor::getTitle).setHeader("Title");
        authorGrid.addComponentColumn(author -> {
            Button removeButton = new Button("Remove");
            removeButton.addClickListener(e -> {
                reportAuthorRepository.delete(author);
                Notification.show("Author removed");
                authorGrid.setItems(reportAuthorRepository.findAll());
            });
            return removeButton;
        });
        reportAuthorsEditor.add(authorGrid);
        TextField nameField = new TextField("Name");
        TextField titleField = new TextField("Title");
        Button addButton = new Button("Add");
        addButton.addClickListener(e -> {
            ReportAuthor author = new ReportAuthor();
            author.setName(nameField.getValue());
            author.setTitle(titleField.getValue());
            reportAuthorRepository.save(author);
            Notification.show("Author added");
            authorGrid.setItems(reportAuthorRepository.findAll());
        });
        HorizontalLayout addAuthorLayout = new HorizontalLayout();
        addAuthorLayout.add(nameField, titleField, addButton);
        reportAuthorsEditor.add(addAuthorLayout);

        horizontalLayout.add(reportAuthorsEditor);
        horizontalLayout.add(addressEditor);
        return horizontalLayout;
    }
}

