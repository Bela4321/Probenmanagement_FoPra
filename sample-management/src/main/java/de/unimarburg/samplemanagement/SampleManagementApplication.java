package de.unimarburg.samplemanagement;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableVaadin
@SpringBootApplication
public class SampleManagementApplication {


	public static void main(String[] args) {
		SpringApplication.run(SampleManagementApplication.class, args);
	}

}
