package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Subject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final List<Subject> subjects = new ArrayList<>();

    public List<Subject> findAll() {
        return subjects;
    }

    public Optional<Subject> findById(int id) {
        return subjects.stream()
                .filter(subject -> subject.getId() == id)
                .findFirst();
    }

    public Subject add(Subject subject) {
        // Generate a unique ID for the new subject (assuming Subject class has setId method)
        Long newId = subjects.isEmpty() ? 1 : subjects.get(subjects.size() - 1).getId() + 1;
        subject.setId(newId);
        subjects.add(subject);
        return subject;
    }

    public Subject update(Subject subject) {
        Optional<Subject> existingSubjectOptional = findById(Math.toIntExact(subject.getId()));
        if (existingSubjectOptional.isPresent()) {
            Subject existingSubject = existingSubjectOptional.get();
            // Update the existing subject with the new details
            existingSubject.setStudyName(subject.getStudyName());
            // Other fields to update if necessary
            return existingSubject;
        } else {
            throw new IllegalArgumentException("Subject not found");
        }
    }

    public void delete(Subject subject) {
        subjects.remove(subject);
    }
}
