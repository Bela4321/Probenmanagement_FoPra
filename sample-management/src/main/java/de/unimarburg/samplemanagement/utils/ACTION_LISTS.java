package de.unimarburg.samplemanagement.utils;

import de.unimarburg.samplemanagement.UI.analyses.AddAnalysisToSamples;
import de.unimarburg.samplemanagement.UI.analyses.AddAnalysisToStudy;
import de.unimarburg.samplemanagement.UI.analyses.AnalysisResultView;
import de.unimarburg.samplemanagement.UI.analyses.InputAnalysisResult;
import de.unimarburg.samplemanagement.UI.sample.ManualSampleEditing;
import de.unimarburg.samplemanagement.UI.sample.SampleDelivery;
import de.unimarburg.samplemanagement.UI.study.CreateStudyReport;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ACTION_LISTS {

    public static List<Pair<String, Class>> getStudySpecificActions() {
        List<Pair<String, Class>> studyActions = new ArrayList<>();
        studyActions.add(new Pair<>("Probenlieferung", SampleDelivery.class));
        studyActions.add(new Pair<>("Manuelle Probenbearbeitung", ManualSampleEditing.class));
        studyActions.add(new Pair<>("Analyse zu Studie hinzufügen", AddAnalysisToStudy.class));
        studyActions.add(new Pair<>("Analyse zu Proben hinzufügen", AddAnalysisToSamples.class));
        //studyActions.add(new Pair<>("Arbeitsplatzliste erstellen","CreateWorkplaceList"));
        studyActions.add(new Pair<>("Probenanalyse eintragen/bearbeiten", InputAnalysisResult.class));
        studyActions.add(new Pair<>("Probenanalysen anschauen", AnalysisResultView.class));
        studyActions.add(new Pair<>("Befund erstellen", CreateStudyReport.class));
        return studyActions;
    }
}
