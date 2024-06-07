package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.repository.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SampleService {

    @Autowired
    private SampleRepository sampleRepository;

    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    public Optional<Sample> findById(Long id) {
        return sampleRepository.findById(id);
    }

    public Sample save(Sample sample) {
        return sampleRepository.save(sample);
    }

    public Sample update(Sample sample) {
        if (sampleRepository.existsById(sample.getId())) {
            return sampleRepository.save(sample);
        } else {
            throw new IllegalArgumentException("Sample not found with id: " + sample.getId());
        }
    }

    public void delete(Sample sample) {
        if (sampleRepository.existsById(sample.getId())) {
            sampleRepository.delete(sample);
        } else {
            throw new IllegalArgumentException("Sample not found with id: " + sample.getId());
        }
    }

    public void deleteById(Long id) {
        if (sampleRepository.existsById(id)) {
            sampleRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Sample not found with id: " + id);
        }
    }
}
