package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "study")
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studyName;

    private Date studyDate;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<Sample> listOfSamples;

    // Constructors
    public Study() {
    }

    public Study(Long id, String studyName, Date studyDate, List<Sample> listOfSamples) {
        this.id = id;
        this.studyName = studyName;
        this.listOfSamples = listOfSamples;
    }


}