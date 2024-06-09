package de.unimarburg.samplemanagement.service;


import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyService {

    StudyRepository studyRepository;

    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public List<Study> findByNameAndBirthDate(){
        return studyRepository.findAll();
    }

    public void  delete(Study study){
        studyRepository.delete(study);
    }

    public void save(Study study){
        if (study == null) {
            System.err.println("Entry is null.");
            return;
        }
        studyRepository.save(study);
    }


    public List<Study> findByNameAndBirthDate(String nameFilter,String dateFilter) {
        if ((nameFilter == null || nameFilter.isEmpty()) && (dateFilter != null || !dateFilter.isEmpty())) {
            return studyRepository.searchDate(dateFilter);
        } else if ((nameFilter != null || !nameFilter.isEmpty()) && (dateFilter == null || dateFilter.isEmpty())) {
            return studyRepository.searchName(nameFilter);
        }
    else {
            return studyRepository.findAll();
        }
    }

}
