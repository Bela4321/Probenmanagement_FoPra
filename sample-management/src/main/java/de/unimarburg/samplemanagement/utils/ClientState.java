package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.SampleDelivery;
import de.unimarburg.samplemanagement.model.Study;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientState{
    Study selectedStudy;
    Sample selectedSample;
    SampleDelivery selectedSampleDelivery;

    public ClientState() {
        this.selectedStudy = null;
        this.selectedSample = null;
        this.selectedSampleDelivery = null;
    }
}
