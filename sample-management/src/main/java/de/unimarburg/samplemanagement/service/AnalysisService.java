package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {


    private final AnalysisRepository analysisRepository;

    @Autowired
    public AnalysisService(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    public void saveAnalysis(Analysis analysis) {
        analysisRepository.save(analysis);
    }
}