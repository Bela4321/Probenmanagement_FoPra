package de.unimarburg.samplemanagement.UI.study;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Route("/create_Study")
public class CreateStudy extends HorizontalLayout {

    @Autowired
    StudyRepository studyRepository;

    public CreateStudy() {

        setVisible(true);
        add(SIDEBAR_FACTORY.getSidebar(null));
        VerticalLayout vl = new VerticalLayout();
        TextField studyname = new TextField("Studienname");
        DatePicker startdate = new DatePicker("Startdatum");
        startdate.setPlaceholder("Startdatum");
        startdate.setValue(LocalDate.now());
        //todo: add more metadatat

        Button save = new Button("Speichern");
        save.addClickListener(e->{
            //save study to database
            saveStudy(studyname.getValue(),startdate.getValue());
        });
        vl.add(studyname,startdate,save);
        add(vl);
    }

    private void saveStudy(String studyname, LocalDate startdate) {
        //save study to database
        if (studyRepository.existsByStudyName(studyname)) {
            Notification.show("Studienname existiert bereits!, bitte wählen Sie einen anderen Namen");
            return;
        }
        Study study = new Study();
        study.setStudyName(studyname);
        study.setStudyDate(GENERAL_UTIL.convertToDate(startdate));
        studyRepository.save(study);
        Notification.show("Studie erfolgreich gespeichert");
    }


}
