package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyService {

    private final StudyRepository studyRepository;

    @Autowired
    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public List<Study> getAllStudies() {
        return studyRepository.findAll();
    }

    public Study getStudyById(Long id) {
        return studyRepository.findById(id).orElse(null);
    }

    public Study saveStudy(Study study) {
        return studyRepository.save(study);
    }

    public void deleteStudy(Long id) {
        studyRepository.deleteById(id);
    }
}
