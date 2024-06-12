package de.unimarburg.samplemanagement;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import de.unimarburg.samplemanagement.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableVaadin
@SpringBootApplication
public class SampleManagementApplication {

	@Autowired
	private MyUserService myUserService;


	public static void main(String[] args) {
		SpringApplication.run(SampleManagementApplication.class, args);
	}

}
