package de.unimarburg.samplemanagement.service;


import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyService {

    @Autowired
    private StudyRepository studyRepository;

    public List<Study> getAllStudies() {
        return studyRepository.findAll();
    }


}
