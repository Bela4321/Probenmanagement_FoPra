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
@IdClass(SubjectCompositeKey.class)
public class Subject {

    @Id
    private Long id;
    @Id
    private Long studyId;


    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Sample> listOfSamples = new ArrayList<>();

    // Constructors
    public Subject() {
    }

    public Subject(Long id, List<Sample> listOfSamples) {
        this.id = id;
        this.listOfSamples = listOfSamples;
    }
}
