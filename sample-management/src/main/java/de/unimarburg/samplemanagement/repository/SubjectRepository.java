package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> getSubjectByIdAndStudyId(Long id, Long studyId);
}
