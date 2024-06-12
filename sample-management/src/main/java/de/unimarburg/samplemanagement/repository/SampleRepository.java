package de.unimarburg.samplemanagement.repository;
import de.unimarburg.samplemanagement.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
}
