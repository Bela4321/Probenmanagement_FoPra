package de.unimarburg.samplemanagement.UI.study;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Sample;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.service.ClientStateService;
import de.unimarburg.samplemanagement.utils.ACTION_LISTS;
import de.unimarburg.samplemanagement.utils.DISPLAY_UTILS;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Route("/StudyOverview")
public class StudyOverview extends HorizontalLayout {

    ClientStateService clientStateService;

    @Autowired
    public StudyOverview(ClientStateService clientStateService) {
        this.clientStateService = clientStateService;
        add(SIDEBAR_FACTORY.getSidebar(clientStateService.getClientState().getSelectedStudy()));
        if (clientStateService.getClientState().getSelectedStudy() == null) {
            add(new Text("No study selected"));
            return;
        }
        Component studystats = getStudyStats(clientStateService.getClientState().getSelectedStudy());
        Component studyActions = getStudyActions(clientStateService.getClientState().getSelectedStudy());
        add(new VerticalLayout(
                new Text("Study Overview"),
                studystats,
                new Text("Actions"),
                studyActions));
    }

    private Component getStudyStats(Study study) {
        //get Facts about the study
        Date studyDate = study.getStartDate();
        int numberOfSamples = study.getListOfSamples().size();
        int numberofSubjects = study.getListOfSamples().stream().map(Sample::getSubject).distinct().toArray().length;
        int numberOfAnalysesCompleted = study.getListOfSamples().stream().map(Sample::getListOfAnalysis).flatMap(List::stream).filter(a->!(a.getAnalysisResult()==null||a.getAnalysisResult().isBlank())).toArray().length;
        int numberOfTotalAnalyses = study.getListOfSamples().stream().map(Sample::getListOfAnalysis).flatMap(List::stream).toArray().length;

        return DISPLAY_UTILS.getBoxAlignment(
                new Text("Study Date: "+studyDate+"\t"),
                new Text("Number of Samples: "+numberOfSamples+"\t"),
                new Text("Number of Subjects: "+numberofSubjects+"\t"),
                new Text("Number of Analyses completed: "+numberOfAnalysesCompleted+"\t"),
                new Text("Number of Total Analyses: "+numberOfTotalAnalyses+"\t")
        );
    }

    private Component getStudyActions(Study study) {
        List<Button> buttons = new ArrayList<>();

        List<Pair<String, Class>> studyActions = ACTION_LISTS.getStudySpecificActions();

        studyActions.forEach(pair->{
            Button button = new Button(pair.getA());
            button.addClickListener(e-> UI.getCurrent().navigate(pair.getB()));
            buttons.add(button);
        });

        return DISPLAY_UTILS.getBoxAlignment(buttons.toArray(new Button[0]));
    }
}
