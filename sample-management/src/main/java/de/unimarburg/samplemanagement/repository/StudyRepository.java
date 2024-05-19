package de.unimarburg.samplemanagement.repository;
import de.unimarburg.samplemanagement.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

}
