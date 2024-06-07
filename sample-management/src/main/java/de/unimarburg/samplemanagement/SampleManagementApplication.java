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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
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

	/*@Bean
	public CommandLineRunner demo(StudyRepository studyRepository,
								  SampleRepository sampleRepository,
								  ParameterRepository parameterRepository,
								  AnalysisRepository analysisRepository,
								  SubjectRepository subjectRepository) {
		return (args) -> {
			// Create instances and set relationships
			Study study = new Study();
			study.setStudyName("Sample Study");
			study.setStudyDate(new Date());

			Subject subject = new Subject();

			// Save the subject and study first
			subjectRepository.save(subject);
			studyRepository.save(study);

			Sample sample1 = new Sample();
			sample1.setStudy(study);
			sample1.setSubject(subject);
			sample1.setCoordinates("0,0");
			sample1.setVisits(1);
			sample1.setSampleDate(new Date());
			sample1.setSample_amount("10ml");
			sample1.setSample_barcode("ABC123");

			Sample sample2 = new Sample();
			sample2.setStudy(study);
			sample2.setSubject(subject);
			sample2.setCoordinates("1,1");
			sample2.setVisits(2);
			sample2.setSampleDate(new Date());
			sample2.setSample_amount("20ml");
			sample2.setSample_barcode("XYZ456");

			subject.setListOfSamples(Arrays.asList(sample1, sample2));
			study.setListOfSamples(Arrays.asList(sample1, sample2));

			Optional<MyUser> userOpt = myUserService.findByUsername("testUser");
			MyUser user;
			if (userOpt.isPresent()) {
				user = userOpt.get();
			} else {
				user = new MyUser();
				user.setUsername("testUser");
				user.setRoles(Arrays.asList("ROLE_USER"));
				myUserService.saveUser(user);
			}

			Parameter parameter = new Parameter();
			parameter.setParameterName("pH");
			parameterRepository.save(parameter);

			Analysis analysis1 = new Analysis();
			analysis1.setSample(sample1);
			analysis1.setUser(user);
			analysis1.setParameterName(parameter.getParameterName());
			analysis1.setAnalysisResult("7.0");
			analysis1.setAnalysisDate(new Date());

			Analysis analysis2 = new Analysis();
			analysis2.setSample(sample2);
			analysis2.setUser(user);
			analysis2.setParameterName(parameter.getParameterName());
			analysis2.setAnalysisResult("8.0");
			analysis2.setAnalysisDate(new Date());

			parameter.setListOfAnalysis(Arrays.asList(analysis1, analysis2));
			sample1.setListOfAnalysis(Arrays.asList(analysis1));
			sample2.setListOfAnalysis(Arrays.asList(analysis2));

			sampleRepository.saveAll(Arrays.asList(sample1, sample2));
			analysisRepository.saveAll(Arrays.asList(analysis1, analysis2));

			// Fetch and verify
			Study fetchedStudy = studyRepository.findById(study.getId()).orElse(null);
			System.out.println("Fetched Study: " + fetchedStudy);

			Subject fetchedSubject = subjectRepository.findById(subject.getId()).orElse(null);
			System.out.println("Fetched Subject: " + fetchedSubject);

			MyUser fetchedUser = myUserService.fetchUserWithInitializedCollections(user.getId());
			System.out.println("Fetched User: " + fetchedUser);

			Parameter fetchedParameter = parameterRepository.findById(parameter.getId()).orElse(null);
			System.out.println("Fetched Parameter: " + fetchedParameter);

			Sample fetchedSample1 = sampleRepository.findById(sample1.getId()).orElse(null);
			System.out.println("Fetched Sample 1: " + fetchedSample1);

			Sample fetchedSample2 = sampleRepository.findById(sample2.getId()).orElse(null);
			System.out.println("Fetched Sample 2: " + fetchedSample2);

			Analysis fetchedAnalysis1 = analysisRepository.findById(analysis1.getId()).orElse(null);
			System.out.println("Fetched Analysis 1: " + fetchedAnalysis1);

			Analysis fetchedAnalysis2 = analysisRepository.findById(analysis2.getId()).orElse(null);
			System.out.println("Fetched Analysis 2: " + fetchedAnalysis2);


			// Testing Excel File input
			String EXCEL_FILE_PATH = "StudyTemplate.xlsx";
			URL resourceUrl = getClass().getClassLoader().getResource(EXCEL_FILE_PATH);
			if (resourceUrl == null) {
				throw new FileNotFoundException("File not found: " + EXCEL_FILE_PATH);
			}

			File file = new File(resourceUrl.getFile());
			FileInputStream inputStream = new FileInputStream(file);

			ExcelParser parser = new ExcelParser(sampleRepository, subjectRepository, studyRepository);
			parser.readExcelFile(inputStream);

			inputStream.close();


		};*/
	}

