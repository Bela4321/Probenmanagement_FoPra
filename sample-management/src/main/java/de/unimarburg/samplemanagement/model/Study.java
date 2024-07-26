package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;
import java.util.List;


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
    private String numberOfSubjects;
    private String abnahmezahl;
    private String assay;
    private String unit;
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




    public List<Sample> getListOfSamples() {
        return listOfSubjects.stream()
                .map(Subject::getListOfSamples)
                .flatMap(List::stream)
                .toList();
    }

    public String getName() {
        return studyName;
    }
}
