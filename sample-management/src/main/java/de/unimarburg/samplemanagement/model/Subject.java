package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@Entity

@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studyName;

    private int idByStudy;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Sample> listOfSamples = new ArrayList<>();

    // Constructors
    public Subject() {
    }

    public Subject(Long id, List<Sample> listOfSamples) {
        this.id = id;
        this.listOfSamples = listOfSamples;
    }

    public Subject(Long id, String studyName, int idByStudy) {
        this.id = id;
        this.studyName = studyName;
        this.idByStudy = idByStudy;
    }
}
