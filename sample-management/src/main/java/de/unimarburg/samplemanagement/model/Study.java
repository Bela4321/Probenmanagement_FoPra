package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "study", uniqueConstraints = {
        @UniqueConstraint(columnNames = "studyName")
})
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String studyName;

    @Temporal(TemporalType.DATE)
    private Date studyDate;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Sample> listOfSamples;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @UniqueElements
    private List<AnalysisType> analysisTypes;

    // Constructors
    public Study() {
    }

    public Study(Long id, String studyName, Date studyDate, List<Sample> listOfSamples) {
        this.id = id;
        this.studyName = studyName;
        this.listOfSamples = listOfSamples;
    }


}
