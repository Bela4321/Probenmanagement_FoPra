package de.unimarburg.samplemanagement.UI.general_info;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.AddressStore;
import de.unimarburg.samplemanagement.repository.AdressStoreRepository;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;


@Route("/edit_adresses")
public class EditAdresses extends HorizontalLayout {


    private final AdressStoreRepository addressStoreRepository;
    private static final String ownAddressKey = "OUR_ADDRESS";

    @Autowired
    public EditAdresses(AdressStoreRepository addressStoreRepository, ClientStateService clientStateService) {
        this.addressStoreRepository = addressStoreRepository;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        add(loadContent());
    }

    private Component loadContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Text("This is our current Adress"));
        TextArea addressField = new TextArea();
        Optional<AddressStore> optionalAddressStore= addressStoreRepository.findAddressStoreByAddressKey(ownAddressKey);
        AddressStore addressStore;
        if (optionalAddressStore.isPresent()) {
            addressStore = optionalAddressStore.get();
        } else {
            addressStore = new AddressStore();
            addressStore.setAddressKey(ownAddressKey);
            addressStore.setAddress("");
        }
        //allow linebreaks in addressfield-> automatic resizing

        addressField.setValue(addressStore.getAddress());
        Button saveButton = new Button("Save");
        saveButton.addClickListener(e -> {
            addressStore.setAddress(addressField.getValue());
            addressStoreRepository.save(addressStore);
            Notification.show("Address saved");
            //refresh the page
            saveButton.getUI().ifPresent(ui -> ui.navigate("edit_adresses"));
        });
        verticalLayout.add(addressField,saveButton);
        return verticalLayout;
    }
}
