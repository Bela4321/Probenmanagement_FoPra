package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.model.SubjectCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, SubjectCompositeKey>, SubjectRepositoryCustom {
}
