package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "sample",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"sample_barcode", "study_id"}))

public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    @NotNull
    private Subject subject;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    @NotNull
    private Study study; // Change here to relate to Study

    private String coordinates;
    private int visits;
    private Date sampleDate;
    private String sample_amount;
    private String sample_barcode;
    private String sample_type;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Analysis> listOfAnalysis;

    private boolean checked; // Add this field

    // Getter and Setter for checked field
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getChecked() {
        return checked;
    }

    public String getStudyName() {
        return study != null ? study.getName() : null;
    }

    public Long getSubjectAlias() {
        return subject != null ? subject.getAlias() : null;
    }

    /**
     * Set the subject and study of the sample
     * @param subject the subject to set (and its study)
     */
    public void setSubject(@NotNull Subject subject) {
        this.subject = subject;
        this.study = subject.getStudy();
    }
}

