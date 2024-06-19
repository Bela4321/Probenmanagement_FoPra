package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;

    @Autowired
    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public List<Sample> getAllSamples() {
        return sampleRepository.findAll();
    }

    public Sample getSampleById(Long id) {
        return sampleRepository.findById(id).orElse(null);
    }

    @Transactional
    public Sample saveSample(Sample sample) {
        return sampleRepository.save(sample);
    }

    @Transactional
    public void deleteSample(Long id) {
        sampleRepository.deleteById(id);
    }
}
