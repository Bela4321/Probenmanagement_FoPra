package de.unimarburg.samplemanagement;

import de.unimarburg.samplemanagement.model.*;
import de.unimarburg.samplemanagement.repository.*;
import de.unimarburg.samplemanagement.service.MyUserService;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@SpringBootApplication
public class SampleManagementApplication {

	@Autowired
	private MyUserService myUserService;


	public static void main(String[] args) {
		SpringApplication.run(SampleManagementApplication.class, args);
	}

}
