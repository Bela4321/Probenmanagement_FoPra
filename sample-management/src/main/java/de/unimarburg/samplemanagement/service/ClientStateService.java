package de.unimarburg.samplemanagement.service;

import com.vaadin.flow.server.VaadinSession;
import de.unimarburg.samplemanagement.utils.ClientState;
import org.springframework.stereotype.Service;

@Service
public class ClientStateService {
    private static final String CLIENT_STATE_KEY = "clientState";

    public ClientState getUserState() {
        if (VaadinSession.getCurrent() == null) {
            //empty state to avoid null pointer exceptions
            return new ClientState();
        }
        ClientState clientState= (ClientState) VaadinSession.getCurrent().getAttribute(CLIENT_STATE_KEY);
        if (clientState == null) {
            clientState = new ClientState();
            VaadinSession.getCurrent().setAttribute(CLIENT_STATE_KEY, clientState);
        }
        return clientState;
    }

    public void setUserState(ClientState clientState) {
        VaadinSession.getCurrent().setAttribute(CLIENT_STATE_KEY, clientState);
    }
}
