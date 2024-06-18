package de.unimarburg.samplemanagement.utils;

import com.vaadin.flow.data.provider.DataKeyMapper;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.Rendering;
import com.vaadin.flow.dom.Element;
import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.Sample;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class GENERAL_UTIL {

    public static String getAnalysisForSample(Sample sample, Long analysisTypeID) {
        try {
            return sample.getListOfAnalysis().stream()
                    .filter(analysis -> analysisTypeID.equals(analysis.getAnalysisType().getId()))
                    .map(Analysis::getAnalysisResult)
                    .findFirst()
                    .orElse("");
        } catch (Exception e) {
            return "Kein Analyseergebnis vorhanden";
        }
    }

    public static Date convertToDate(LocalDate localDate) {
        // Convert LocalDate to an Instant
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        // Create a Date from the Instant
        return Date.from(instant);
    }

    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Renderer<Sample> renderDate() {
        return new LocalDateRenderer<>(sample -> convertToLocalDate(sample.getSampleDate()), "dd.MM.yyyy");
    }
}
