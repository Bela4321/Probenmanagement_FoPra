package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Subject;
import de.unimarburg.samplemanagement.model.SubjectCompositeKey;
import de.unimarburg.samplemanagement.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findById(Long id, Long studyId) {
        SubjectCompositeKey key = new SubjectCompositeKey();
        key.setId(id);
        key.setStudyId(studyId);
        return subjectRepository.findById(key);
    }

    public List<Subject> searchByKeyword(String keyword) {
        return subjectRepository.searchByIdKeyword(keyword);
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void deleteById(Long id, Long studyId) {
        SubjectCompositeKey key = new SubjectCompositeKey();
        key.setId(id);
        key.setStudyId(studyId);
        subjectRepository.deleteById(key);
    }

    public Subject update(Subject existingSubject, Subject newSubjectData) {
        existingSubject.setListOfSamples(newSubjectData.getListOfSamples());
        // Add other fields that need to be updated
        return subjectRepository.save(existingSubject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}
