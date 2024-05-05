package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long> {

    MyUser findByUsername(String username);
    MyUser findById(long id);

}
