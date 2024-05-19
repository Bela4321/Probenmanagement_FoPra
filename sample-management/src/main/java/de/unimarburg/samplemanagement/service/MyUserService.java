package de.unimarburg.samplemanagement.service;

import de.unimarburg.samplemanagement.model.MyUser;
import de.unimarburg.samplemanagement.repository.MyUserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MyUserService {

    @Autowired
    private MyUserRepository userRepository;

    @Transactional
    public MyUser saveUser(MyUser user) {
        return userRepository.save(user);
    }

    @Transactional
    public MyUser fetchUserWithInitializedCollections(Long userId) {
        Optional<MyUser> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            MyUser user = userOpt.get();
            Hibernate.initialize(user.getListOfAnalysis());
            return user;
        }
        return null;
    }

    public Optional<MyUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<MyUser> findById(Long id) {
        return userRepository.findById(id);
    }
}
