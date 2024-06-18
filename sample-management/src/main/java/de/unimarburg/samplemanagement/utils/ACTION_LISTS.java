package de.unimarburg.samplemanagement.utils;

import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ACTION_LISTS {

    public static List<Pair<String, String>> getStudySpecificActions() {
        List<Pair<String, String>> studyActions = new ArrayList<>();
        studyActions.add(new Pair<>("Probenlieferung","SampleDelivery"));
        studyActions.add(new Pair<>("Manuelle Probenbearbeitung","ManualSampleEditing"));
        studyActions.add(new Pair<>("Analyse zu Studie hinzufügen","AddSampleAnalysisToStudy"));
        studyActions.add(new Pair<>("Analyse zu Proben hinzufügen","AddAnalysisToSample"));
        //studyActions.add(new Pair<>("Arbeitsplatzliste erstellen","CreateWorkplaceList"));
        studyActions.add(new Pair<>("Probenanalyse eintragen/bearbeiten","EnterSampleAnalysis"));
        studyActions.add(new Pair<>("Probenanalysen anschauen","ViewSampleAnalysis"));
        studyActions.add(new Pair<>("Befund erstellen","CreateReport"));
        return studyActions;
    }
}
