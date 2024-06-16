package de.unimarburg.samplemanagement.repository;


import de.unimarburg.samplemanagement.model.AnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisTypeRepository extends JpaRepository<AnalysisType,Long> {
    public boolean existsByAnalysisName(String name);
}
