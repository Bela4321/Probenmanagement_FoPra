package de.unimarburg.samplemanagement.service;

import com.vaadin.flow.server.VaadinSession;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.utils.ClientState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientStateService {
    private static final String CLIENT_STATE_KEY = "clientState";
    private final StudyRepository studyRepository;

    @Autowired
    public ClientStateService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public ClientState getClientState() {
        if (VaadinSession.getCurrent() == null) {
            //empty state to avoid null pointer exceptions
            return new ClientState();
        }
        ClientState clientState= (ClientState) VaadinSession.getCurrent().getAttribute(CLIENT_STATE_KEY);
        if (clientState == null) {
            clientState = new ClientState();
            VaadinSession.getCurrent().setAttribute(CLIENT_STATE_KEY, clientState);
        } else if (clientState.getSelectedStudy() != null) {
            //reload study from database
            Study study = studyRepository.findById(clientState.getSelectedStudy().getId()).orElse(null);
            clientState.setSelectedStudy(study);
        }
        return clientState;
    }

    public void setUserState(ClientState clientState) {
        VaadinSession.getCurrent().setAttribute(CLIENT_STATE_KEY, clientState);
    }
}
