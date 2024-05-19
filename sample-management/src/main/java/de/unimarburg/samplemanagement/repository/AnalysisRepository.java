package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis,Long> {
}
