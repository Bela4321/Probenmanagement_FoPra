package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.UI.analyses.*;
import de.unimarburg.samplemanagement.UI.sample.ManualSampleEditing;
import de.unimarburg.samplemanagement.UI.sample.SampleDelivery;
import de.unimarburg.samplemanagement.UI.sample.VerifySampleDelivery;
import de.unimarburg.samplemanagement.UI.study.CreateStudyReport;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ACTION_LISTS {

    public static List<Pair<String, Class>> getStudySpecificActions() {
        List<Pair<String, Class>> studyActions = new ArrayList<>();
        studyActions.add(new Pair<>("Sample Delivery", SampleDelivery.class));
        studyActions.add(new Pair<>("Verify Sample Delivery", VerifySampleDelivery.class));
        studyActions.add(new Pair<>("Edit Samples", ManualSampleEditing.class));
        studyActions.add(new Pair<>("Add Analysis to Study", AddAnalysisToStudy.class));
        studyActions.add(new Pair<>("Add Analysis to Samples", AddAnalysisToSamples.class));
        studyActions.add(new Pair<>("Create Workplace Lists", CreateWorkplaceList.class));
        studyActions.add(new Pair<>("Input/Edit Analysis Results", InputAnalysisResult.class));
        studyActions.add(new Pair<>("View Sample Analysis", AnalysisResultView.class));
        studyActions.add(new Pair<>("Create Study Report", CreateStudyReport.class));
        studyActions.add(new Pair<>("Upload Analysis Results", ReadResults.class));
        return studyActions;
    }
}
