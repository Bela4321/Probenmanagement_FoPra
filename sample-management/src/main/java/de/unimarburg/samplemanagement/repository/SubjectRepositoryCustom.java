package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Subject;

import java.util.List;

public interface SubjectRepositoryCustom {
    List<Subject> searchByIdKeyword(String keyword);
}
