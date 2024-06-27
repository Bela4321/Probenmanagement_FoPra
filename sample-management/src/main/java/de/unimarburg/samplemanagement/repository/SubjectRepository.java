package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.model.Subject;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> getSubjectByAliasAndStudy(@NotNull Long alias, @NotNull Study study);
}
