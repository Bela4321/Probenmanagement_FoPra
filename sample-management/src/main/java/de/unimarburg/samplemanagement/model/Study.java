package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "study", uniqueConstraints = {
        @UniqueConstraint(columnNames = "study_name")
})
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String studyName;

    @Temporal(TemporalType.DATE)
    private Date studyDate;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Subject> listOfSubjects;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SampleDelivery> sampleDeliveryList;

    @ManyToMany(fetch = FetchType.EAGER)
    @UniqueElements
    private List<AnalysisType> analysisTypes;


    public List<Sample> getListOfSamples() {
        return listOfSubjects.stream()
                .map(Subject::getListOfSamples)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getName() {
        return studyName;
    }
}
