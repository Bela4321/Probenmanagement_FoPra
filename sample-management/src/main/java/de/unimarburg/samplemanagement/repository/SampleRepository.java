package de.unimarburg.samplemanagement.repository;
import de.unimarburg.samplemanagement.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    List<Sample> getSampleByStudyId(Long studyId);

}
