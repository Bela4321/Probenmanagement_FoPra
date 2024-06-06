package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.Parameter;
import de.unimarburg.samplemanagement.repository.ParameterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParameterService {
    private final ParameterRepository parameterRepository;

    @Autowired
    public ParameterService(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Transactional
    public Parameter getParameterWithAnalysis(Long paramID) {
        Optional<Parameter> paramOpt = parameterRepository.findById(paramID);
        if (paramOpt.isPresent()) {
            Parameter parameter = paramOpt.get();
            Hibernate.initialize(parameter.getListOfAnalysis());
            List<Analysis> listOfAnalysis= parameter.getListOfAnalysis();
            for (Analysis analysis : listOfAnalysis) {
                Hibernate.initialize(analysis.getSample());
            }
            return parameter;
        }
        throw new EntityNotFoundException("No Analysis for this Parameter" + paramID);
    }

}
