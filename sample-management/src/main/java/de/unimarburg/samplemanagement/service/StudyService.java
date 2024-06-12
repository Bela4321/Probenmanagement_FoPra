package de.unimarburg.samplemanagement.service;


import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.MyUser;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudyService {

    @Autowired
    private StudyRepository studyRepository;

    public List<Study> getAllStudies() {
        return studyRepository.findAll();
    }

    @Transactional
    public Study getStudyWithSamples(Long studyId) {
        Optional<Study> studyOpt = studyRepository.findById(studyId);
        if (studyOpt.isPresent()) {
            Study study = studyOpt.get();
            Hibernate.initialize(study.getListOfSamples());
            List<Sample> sampleList= study.getListOfSamples();
            for (Sample sample : sampleList) {
                Hibernate.initialize(sample.getListOfAnalysis());
                Hibernate.initialize(sample.getStudy());
                Hibernate.initialize(sample.getSubject());
                List<Analysis> analysisList = sample.getListOfAnalysis();
                for (Analysis analysis : analysisList) {
                    Hibernate.initialize(analysis.getSample());
                    Hibernate.initialize(analysis.getUser());
                    Hibernate.initialize(analysis.getAnalysisType());
                }
            }
            return study;
        }
        throw new EntityNotFoundException("No Samples in selected Study" + studyId);
    }


}
