package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "subject", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"alias", "study_id"})
})
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Setter for alias
    @Setter
    @Getter
    @NotNull
    private Long alias;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    @NotNull
    private Study study;


    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Sample> listOfSamples = new ArrayList<>();

    public Subject(Long alias, Study study) {
        this.alias = alias;
        this.study = study;
    }
}
