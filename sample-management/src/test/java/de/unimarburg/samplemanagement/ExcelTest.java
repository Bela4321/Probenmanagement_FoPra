package de.unimarburg.samplemanagement;

import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.utils.ExcelParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExcelTest {


    private static final String EXCEL_FILE_PATH = "src/main/resources/StudyTemplate.xlsx";

    @Test
    void testReadExcelFile() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(EXCEL_FILE_PATH)) {

            Study study = new Study();

            // Verify the study name
            assertEquals("ExampleName", study.getStudyName());

            // Verify the list of samples
            List<Sample> sampleList = study.getListOfSamples();
            assertNotNull(sampleList);
            assertEquals(4, sampleList.size());

            // Verify the details of the first sample
            Sample firstSample = sampleList.get(0);
            assertEquals("A1", firstSample.getCoordinates());
            assertEquals(2, firstSample.getVisits());
            assertNotNull(firstSample.getSampleDate());
            assertEquals("700 µl", firstSample.getSample_amount());
            //assertEquals("SampleType1", firstSample.getSampleType());
            assertEquals("rdherhya", firstSample.getSample_barcode());

            // Verify the details of the second sample (if necessary)
            Sample secondSample = sampleList.get(1);
            assertEquals("A2", secondSample.getCoordinates());
            assertEquals(2, secondSample.getVisits());
            assertNotNull(secondSample.getSampleDate());
            assertEquals("700 µl", secondSample.getSample_amount());
            //assertEquals("SampleType2", secondSample.getSampleType());
            assertEquals("sdgerh5", secondSample.getSample_barcode());
        }
    }
}
