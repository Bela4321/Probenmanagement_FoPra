package de.unimarburg.samplemanagement;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import de.unimarburg.samplemanagement.model.*;
import de.unimarburg.samplemanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@EnableVaadin
@SpringBootApplication
public class SampleManagementApplication {


	public static void main(String[] args) {
		SpringApplication.run(SampleManagementApplication.class, args);
	}


	@Bean
	@Transactional
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
			study1 = studyRepository.save(study1);
			study2 = studyRepository.save(study2);
			study3 = studyRepository.save(study3);

			AnalysisType type1 = new AnalysisType();
			type1.setAnalysisName("Analysis 1");
			type1.setAnalysisDescription("This Analysis tests for something");
			AnalysisType type2 = new AnalysisType();
			type2.setAnalysisName("Analysis 2");
			type2.setAnalysisDescription("This Analysis tests for something else");
			AnalysisType type3 = new AnalysisType();
			type3.setAnalysisName("Analysis 3");
			type3.setAnalysisDescription("This Analysis tests for something else again");
			AnalysisType type4 = new AnalysisType();
			type4.setAnalysisName("Analysis 4");
			type4.setAnalysisDescription("Yet another Analysis");
			type1 = analysisTypeRepository.save(type1);
			type2 = analysisTypeRepository.save(type2);
			type3 = analysisTypeRepository.save(type3);
			type4 = analysisTypeRepository.save(type4);

			study1.setAnalysisTypes(Arrays.asList(type1, type2));
			study2.setAnalysisTypes(Arrays.asList(type3, type4));
			study3.setAnalysisTypes(Arrays.asList(type1, type3, type4));

			Subject subject = new Subject();
			subject.setAlias(1L);
			subject.setStudy(study1);
			Subject subject2 = new Subject();
			subject2.setAlias(2L);
			subject2.setStudy(study2);
			Subject subject3 = new Subject();
			subject3.setAlias(1L);
			subject3.setStudy(study3);
			Subject subject4 = new Subject();
			subject4.setAlias(2L);
			subject4.setStudy(study3);
			Subject subject5 = new Subject();
			subject5.setAlias(3L);
			subject5.setStudy(study3);
			subject = subjectRepository.save(subject);
			subject2 = subjectRepository.save(subject2);
			subject3 = subjectRepository.save(subject3);
			subject4 = subjectRepository.save(subject4);
			subject5 = subjectRepository.save(subject5);

			study1.setListOfSubjects(Arrays.asList(subject));
			study2.setListOfSubjects(Arrays.asList(subject2));
			study3.setListOfSubjects(Arrays.asList(subject3, subject4, subject5));

			Sample sample1 = new Sample();
			sample1.setSubject(subject);
			sample1.setCoordinates("0,0");
			sample1.setVisits(1);
			sample1.setSampleDate(new Date());
			sample1.setSample_amount("10ml");
			sample1.setSample_barcode("ABC123");
			sample1.setSample_type("plasma");
			Sample sample2 = new Sample();
			sample2.setSubject(subject);
			sample2.setCoordinates("1,1");
			sample2.setVisits(2);
			sample2.setSampleDate(new Date());
			sample2.setSample_amount("20ml");
			sample2.setSample_barcode("XYZ456");
			sample2.setSample_type("serum");
			Sample sample3 = new Sample();
			sample3.setSubject(subject2);
			sample3.setCoordinates("5,2");
			sample3.setVisits(7);
			sample3.setSampleDate(new Date());
			sample3.setSample_amount("100ml");
			sample3.setSample_barcode("ABCDEF123");
			sample3.setSample_type("plasma");
			Sample sample4 = new Sample();
			sample4.setSubject(subject3);
			sample4.setCoordinates("1,1");
			sample4.setVisits(2);
			sample4.setSampleDate(new Date());
			sample4.setSample_amount("20ml");
			sample4.setSample_barcode("XYZ45678");
			sample4.setSample_type("serum");
			Sample sample5 = new Sample();
			sample5.setSubject(subject3);
			sample5.setCoordinates("5,0");
			sample5.setVisits(5);
			sample5.setSampleDate(new Date());
			sample5.setSample_amount("80ml");
			sample5.setSample_barcode("ABCDEF12345");
			sample5.setSample_type("blood");
			Sample sample6 = new Sample();
			sample6.setSubject(subject4);
			sample6.setCoordinates("1,1");
			sample6.setVisits(2);
			sample6.setSampleDate(new Date());
			sample6.setSample_amount("200ml");
			sample6.setSample_barcode("XYZ458");
			sample6.setSample_type("plasma");
			sample1 = sampleRepository.save(sample1);
			sample2 = sampleRepository.save(sample2);
			sample3 = sampleRepository.save(sample3);
			sample4 = sampleRepository.save(sample4);
			sample5 = sampleRepository.save(sample5);
			sample6 = sampleRepository.save(sample6);
			sampleRepository.save(sample1);
			sampleRepository.save(sample2);
			sampleRepository.save(sample3);
			sampleRepository.save(sample4);
			sampleRepository.save(sample5);
			sampleRepository.save(sample6);

			subject.setListOfSamples(Arrays.asList(sample1, sample2));
			subject2.setListOfSamples(Arrays.asList(sample3));
			subject3.setListOfSamples(Arrays.asList(sample4, sample5));
			subject4.setListOfSamples(Arrays.asList(sample6));

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
			Analysis analysis4 = new Analysis();
			analysis4.setSample(sample3);
			analysis4.setAnalysisType(type3);
			analysis4.setAnalysisResult("5.0");
			analysis4.setAnalysisDate(new Date());
			Analysis analysis5 = new Analysis();
			analysis5.setSample(sample3);
			analysis5.setAnalysisType(type4);
			analysis5.setAnalysisResult("3.0");
			analysis5.setAnalysisDate(new Date());
			Analysis analysis6 = new Analysis();
			analysis6.setSample(sample4);
			analysis6.setAnalysisType(type1);
			analysis6.setAnalysisResult("7.0");
			analysis6.setAnalysisDate(new Date());
			Analysis analysis7 = new Analysis();
			analysis7.setSample(sample4);
			analysis7.setAnalysisType(type3);
			analysis7.setAnalysisResult("5.0");
			analysis7.setAnalysisDate(new Date());
			Analysis analysis8 = new Analysis();
			analysis8.setSample(sample4);
			analysis8.setAnalysisType(type4);
			analysis8.setAnalysisResult("3.0");
			analysis8.setAnalysisDate(new Date());
			Analysis analysis9 = new Analysis();
			analysis9.setSample(sample5);
			analysis9.setAnalysisType(type1);
			analysis9.setAnalysisResult("");
			analysis9.setAnalysisDate(new Date());
			Analysis analysis10 = new Analysis();
			analysis10.setSample(sample5);
			analysis10.setAnalysisType(type3);
			analysis10.setAnalysisResult("");
			analysis10.setAnalysisDate(new Date());
			Analysis analysis11 = new Analysis();
			analysis11.setSample(sample5);
			analysis11.setAnalysisType(type4);
			analysis11.setAnalysisResult("");
			analysis11.setAnalysisDate(new Date());
			Analysis analysis12 = new Analysis();
			analysis12.setSample(sample6);
			analysis12.setAnalysisType(type1);
			analysis12.setAnalysisResult("7.0");
			analysis12.setAnalysisDate(new Date());
			Analysis analysis13 = new Analysis();
			analysis13.setSample(sample6);
			analysis13.setAnalysisType(type3);
			analysis13.setAnalysisResult("5.0");
			analysis13.setAnalysisDate(new Date());
			Analysis analysis14 = new Analysis();
			analysis14.setSample(sample6);
			analysis14.setAnalysisType(type4);
			analysis14.setAnalysisResult("3.0");
			analysis14.setAnalysisDate(new Date());
			analysis1 = analysisRepository.save(analysis1);
			analysis2 = analysisRepository.save(analysis2);
			analysis3 = analysisRepository.save(analysis3);
			analysis4 = analysisRepository.save(analysis4);
			analysis5 = analysisRepository.save(analysis5);
			analysis6 = analysisRepository.save(analysis6);
			analysis7 = analysisRepository.save(analysis7);
			analysis8 = analysisRepository.save(analysis8);
			analysis9 = analysisRepository.save(analysis9);
			analysis10 = analysisRepository.save(analysis10);
			analysis11 = analysisRepository.save(analysis11);
			analysis12 = analysisRepository.save(analysis12);
			analysis13 = analysisRepository.save(analysis13);
			analysis14 = analysisRepository.save(analysis14);

			sample1.setListOfAnalysis(Arrays.asList(analysis1, analysis2));
			sample2.setListOfAnalysis(Arrays.asList(analysis3));
			sample3.setListOfAnalysis(Arrays.asList(analysis4, analysis5));
			sample4.setListOfAnalysis(Arrays.asList(analysis6, analysis7, analysis8));
			sample5.setListOfAnalysis(Arrays.asList(analysis9, analysis10, analysis11));
			sample6.setListOfAnalysis(Arrays.asList(analysis12, analysis13, analysis14));

			study1 = studyRepository.save(study1);
			study2 = studyRepository.save(study2);
			study3 = studyRepository.save(study3);
		};
	}
}
