package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.ReportAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportAuthorRepository extends JpaRepository<ReportAuthor, Long> {

}
