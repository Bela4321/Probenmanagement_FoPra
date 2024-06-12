package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Study;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientState{
    Study selectedStudy;

    public ClientState() {
        this.selectedStudy = null;
    }
}
