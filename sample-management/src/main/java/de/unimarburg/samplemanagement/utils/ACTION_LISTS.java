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
        studyActions.add(new Pair<>("Probenlieferung", SampleDelivery.class));
        studyActions.add(new Pair<>("Probenlieferung verifizieren", VerifySampleDelivery.class));
        studyActions.add(new Pair<>("Manuelle Probenbearbeitung", ManualSampleEditing.class));
        studyActions.add(new Pair<>("Analyse zu Studie hinzufügen", AddAnalysisToStudy.class));
        studyActions.add(new Pair<>("Analyse zu Proben hinzufügen", AddAnalysisToSamples.class));
        studyActions.add(new Pair<>("Arbeitsplatzliste erstellen", CreateWorkplaceList.class));
        studyActions.add(new Pair<>("Probenanalyse eintragen/bearbeiten", InputAnalysisResult.class));
        studyActions.add(new Pair<>("Probenanalysen anschauen", AnalysisResultView.class));
        studyActions.add(new Pair<>("Befund erstellen", CreateStudyReport.class));
        studyActions.add(new Pair<>("AnalyseErgebnisse Hochladen", ReadResults.class));
        return studyActions;
    }
}
