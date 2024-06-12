package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.model.Analysis;
import de.unimarburg.samplemanagement.model.Sample;

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
}
