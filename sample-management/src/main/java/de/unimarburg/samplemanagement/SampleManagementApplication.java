package de.unimarburg.samplemanagement;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import de.unimarburg.samplemanagement.model.*;
import de.unimarburg.samplemanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;

@EnableVaadin
@SpringBootApplication
public class SampleManagementApplication {


	public static void main(String[] args) {
		SpringApplication.run(SampleManagementApplication.class, args);
	}


	@Bean
	public CommandLineRunner demo(StudyRepository studyRepository,
								  SampleRepository sampleRepository,
								  AnalysisRepository analysisRepository,
								  AnalysisTypeRepository analysisTypeRepository,
								  SubjectRepository subjectRepository) {
		return (args) -> {
			Study study1 = new Study();
			study1.setStudyName("Sample Study 1");
			study1.setStudyDate(new Date());

			Study study2 = new Study();
			study2.setStudyName("Sample Study 2");
			study2.setStudyDate(new Date());

			Study study3 = new Study();
			study3.setStudyName("Sample Study 3");
			study3.setStudyDate(new Date());

			Subject subject = new Subject();
			subject.setId(1L);
			subject.setStudyId(1L);
			Subject subject2 = new Subject();
			subject2.setId(2L);
			subject2.setStudyId(2L);
			Subject subject3 = new Subject();
			subject3.setId(3L);
			subject3.setStudyId(3L);

			subjectRepository.save(subject);
			subjectRepository.save(subject2);
			subjectRepository.save(subject3);
			studyRepository.save(study1);
			studyRepository.save(study2);
			studyRepository.save(study3);

			Sample sample1 = new Sample();
			sample1.setStudy(study1);
			sample1.setSubject(subject);
			sample1.setCoordinates("0,0");
			sample1.setVisits(1);
			sample1.setSampleDate(new Date());
			sample1.setSample_amount("10ml");
			sample1.setSample_barcode("ABC123");

			Sample sample2 = new Sample();
			sample2.setStudy(study1);
			sample2.setSubject(subject);
			sample2.setCoordinates("1,1");
			sample2.setVisits(2);
			sample2.setSampleDate(new Date());
			sample2.setSample_amount("20ml");
			sample2.setSample_barcode("XYZ456");

			subject.setListOfSamples(Arrays.asList(sample1, sample2));
			study1.setListOfSamples(Arrays.asList(sample1, sample2));

			Sample sample3 = new Sample();
			sample3.setStudy(study2);
			sample3.setSubject(subject2);
			sample3.setCoordinates("5,2");
			sample3.setVisits(7);
			sample3.setSampleDate(new Date());
			sample3.setSample_amount("100ml");
			sample3.setSample_barcode("ABCDEF123");

			Sample sample4 = new Sample();
			sample4.setStudy(study2);
			sample4.setSubject(subject);
			sample4.setCoordinates("1,1");
			sample4.setVisits(2);
			sample4.setSampleDate(new Date());
			sample4.setSample_amount("20ml");
			sample4.setSample_barcode("XYZ45678");

			subject2.setListOfSamples(Arrays.asList(sample3, sample4));
			study2.setListOfSamples(Arrays.asList(sample3, sample4));

			Sample sample5 = new Sample();
			sample5.setStudy(study3);
			sample5.setSubject(subject3);
			sample5.setCoordinates("5,0");
			sample5.setVisits(5);
			sample5.setSampleDate(new Date());
			sample5.setSample_amount("80ml");
			sample5.setSample_barcode("ABCDEF12345");

			Sample sample6 = new Sample();
			sample6.setStudy(study2);
			sample6.setSubject(subject);
			sample6.setCoordinates("1,1");
			sample6.setVisits(2);
			sample6.setSampleDate(new Date());
			sample6.setSample_amount("200ml");
			sample6.setSample_barcode("XYZ458");

			subject3.setListOfSamples(Arrays.asList(sample5, sample6));
			study3.setListOfSamples(Arrays.asList(sample5, sample6));


			AnalysisType type1 = new AnalysisType();
			type1.setAnalysisName("Analysis 1");
			type1.setAnalysisDescription("Test 1");
			analysisTypeRepository.save(type1);

			AnalysisType type2 = new AnalysisType();
			type2.setAnalysisName("Analysis 2");
			type2.setAnalysisDescription("Test 2");
			analysisTypeRepository.save(type2);

			AnalysisType type3 = new AnalysisType();
			type2.setAnalysisName("Analysis 3");
			type2.setAnalysisDescription("Test 3");
			analysisTypeRepository.save(type3);


			Analysis analysis1 = new Analysis();
			analysis1.setSample(sample1);
			analysis1.setAnalysisType(type1);
			analysis1.setAnalysisResult("7.0");
			analysis1.setAnalysisDate(new Date());

			Analysis analysis2 = new Analysis();
			analysis2.setSample(sample1);
			analysis2.setAnalysisType(type2);
			analysis2.setAnalysisResult("100.0");
			analysis2.setAnalysisDate(new Date());

			Analysis analysis3 = new Analysis();
			analysis3.setSample(sample2);
			analysis3.setAnalysisType(type1);
			analysis3.setAnalysisResult("13.0");
			analysis3.setAnalysisDate(new Date());


			sample1.setListOfAnalysis(Arrays.asList(analysis1, analysis2));
			sample2.setListOfAnalysis(Arrays.asList(analysis3));

			sampleRepository.saveAll(Arrays.asList(sample1, sample2, sample3, sample4, sample5, sample6));
			analysisRepository.saveAll(Arrays.asList(analysis1, analysis2, analysis3));


		};
	}
}
