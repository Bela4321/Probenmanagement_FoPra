package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@Entity

@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Sample> listOfSamples;

    // Constructors
    public Subject() {
    }

    public Subject(Long id, List<Sample> listOfSamples) {
        this.id = id;
        this.listOfSamples = listOfSamples;
    }
}
