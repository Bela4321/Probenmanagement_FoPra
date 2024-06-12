package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.MyUser;
import de.unimarburg.samplemanagement.model.Study;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
public class ClientState{
    MyUser user;
    Study selectedStudy;

    public ClientState() {
        this.user = null;
        this.selectedStudy = null;
    }
}
