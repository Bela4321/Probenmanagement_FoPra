package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "analysis")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analysisType_id", referencedColumnName = "id")
    @NotNull
    private AnalysisType analysisType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sample_id", referencedColumnName = "id")
    @NotNull
    private Sample sample;

    @Temporal(TemporalType.TIMESTAMP)
    private Date analysisDate;

    private String analysisResult;

    private String Comments;

    public Analysis(AnalysisType analysisType, Sample sample) {
        this.analysisType = analysisType;
        this.sample = sample;
    }
}
