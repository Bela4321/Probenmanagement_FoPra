package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
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
    private String expectedNumberOfSubjects;
    private String expectedNumberOfSampeDeliveries;
    private String sender1;
    private String sender2;
    private String sender3;
    private String sponsor;
    private String remark;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Subject> listOfSubjects;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SampleDelivery> sampleDeliveryList;

    @ManyToMany(fetch = FetchType.EAGER)
    @UniqueElements
    private List<AnalysisType> analysisTypes;


    public int getNumberOfSubjects() {
        return listOfSubjects.size();
    }


    public List<Sample> getListOfSamples() {
        return listOfSubjects.stream()
                .map(Subject::getListOfSamples)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }



    public String getName() {
        return studyName;
    }
    public List<AnalysisType> getAnalysisTypes() {
        return analysisTypes;
    }

}
