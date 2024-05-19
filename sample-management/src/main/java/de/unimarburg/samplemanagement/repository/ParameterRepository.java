package de.unimarburg.samplemanagement.repository;
import com.vaadin.flow.component.html.Param;
import de.unimarburg.samplemanagement.model.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterRepository extends  JpaRepository<Parameter, Long> {
}
