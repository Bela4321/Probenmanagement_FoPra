package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@Table(name = "analysis_type")
public class AnalysisType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String analysisName;

    private String analysisDescription;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AnalysisType)) {
            return false;
        }
        if (Objects.equals(this.getId(), ((AnalysisType) obj).getId())) {
            return true;
        }
        return false;
    }
}

