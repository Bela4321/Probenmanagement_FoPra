package de.unimarburg.samplemanagement.utils;

import java.util.HashMap;
import java.util.Map;

public class ACTION_LISTS {

    public static Map<String, String> getStudySpecificActions() {
        Map<String,String> studyActions = new HashMap<>();
        studyActions.put("Probenlieferung","SampleDelivery");
        studyActions.put("Manuelle Probenbearbeitung","ManualSampleProcessing");
        studyActions.put("Arbeitsplatzliste erstellen","CreateWorkplaceList");
        studyActions.put("Probenanalyse hinzufügen","AddSampleAnalysis");
        studyActions.put("Probenanalyse eintragen","EnterSampleAnalysis");
        studyActions.put("Probenanalyse bearbeiten","EditSampleAnalysis");
        studyActions.put("Befund erstellen","CreateReport");
        return studyActions;
    }
}
