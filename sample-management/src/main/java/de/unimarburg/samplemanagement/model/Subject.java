package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@Entity
@Table(name = "subject", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"alias", "study_id"})
})
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long alias;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    @NotNull
    private Study study;


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
