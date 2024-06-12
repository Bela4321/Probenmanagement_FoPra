package de.unimarburg.samplemanagement.utils;

import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACTION_LISTS {

    public static List<Pair<String, String>> getStudySpecificActions() {
        List<Pair<String, String>> studyActions = new ArrayList<>();
        studyActions.add(new Pair<>("Probenlieferung","SampleDelivery"));
        studyActions.add(new Pair<>("Manuelle Probenbearbeitung","ManualSampleProcessing"));
        studyActions.add(new Pair<>("Analyse zu Proben hinzufügen","AddAnalysisToSample"));
        //studyActions.add(new Pair<>("Arbeitsplatzliste erstellen","CreateWorkplaceList"));
        studyActions.add(new Pair<>("Probenanalyse hinzufügen","AddSampleAnalysis"));
        studyActions.add(new Pair<>("Probenanalyse eintragen","EnterSampleAnalysis"));
        studyActions.add(new Pair<>("Probenanalysen anschauen","ViewSampleAnalysis"));
        studyActions.add(new Pair<>("Befund erstellen","CreateReport"));
        return studyActions;
    }
}
