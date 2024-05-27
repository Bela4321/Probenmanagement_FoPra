package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "sample")
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    private Study study; // Change here to relate to Study

    private String coordinates;
    private int visits;
    private Date sampleDate;
    private String sample_amount;
    private String sample_barcode;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private List<Analysis> listOfAnalysis;

    // Constructors, getters, and setters

    public Sample(Subject subject, String coordinates, int visits, Date sampleDate, String sample_amount, String sample_barcode) {
        this.subject = subject;
        this.coordinates = coordinates;
        this.visits = visits;
        this.sampleDate = sampleDate;
        this.sample_amount = sample_amount;
        this.sample_barcode = sample_barcode;
    }
}

