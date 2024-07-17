package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class SampleDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    Study study;

    @OneToMany(mappedBy = "sampleDelivery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Sample> samples;

    @Temporal(TemporalType.DATE)
    private Date deliveryDate;


    public int getRunningNumber() {
        List<SampleDelivery> sampleDeliveries = study.getSampleDeliveryList();
        return sampleDeliveries.indexOf(this);
    }

    public void addSample(Sample sample) {
        if (samples == null) {
            samples = new ArrayList<>();
        }
        samples.add(sample);
        sample.setSampleDelivery(this);
    }
}
