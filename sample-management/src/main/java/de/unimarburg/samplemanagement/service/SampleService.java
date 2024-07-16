package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;

    @Autowired
    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    public void save(Sample sample) {
        sampleRepository.save(sample);
    }

    // Other service methods if needed
}
