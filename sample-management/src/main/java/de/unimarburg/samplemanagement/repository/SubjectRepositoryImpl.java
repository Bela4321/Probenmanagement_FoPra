package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.Subject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SubjectRepositoryImpl implements SubjectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Subject> searchByIdKeyword(String keyword) {
        String jpql = "SELECT s FROM Subject s WHERE CAST(s.id AS string) LIKE :keyword";
        TypedQuery<Subject> query = entityManager.createQuery(jpql, Subject.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }
}
