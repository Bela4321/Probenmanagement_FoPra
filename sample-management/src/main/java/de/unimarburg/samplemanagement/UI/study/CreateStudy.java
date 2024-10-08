package de.unimarburg.samplemanagement.UI.study;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import de.unimarburg.samplemanagement.utils.DISPLAY_UTILS;
import de.unimarburg.samplemanagement.utils.GENERAL_UTIL;
import de.unimarburg.samplemanagement.utils.SIDEBAR_FACTORY;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route("/create_Study")
public class CreateStudy extends HorizontalLayout {

    @Autowired
    StudyRepository studyRepository;

    public CreateStudy() {

        setVisible(true);
        add(SIDEBAR_FACTORY.getSidebar(null));
        VerticalLayout vl = new VerticalLayout();
        TextField studyname = new TextField("Study Name");
        TextField numberOfSubjects = new TextField("Expected Number Of Subjects");
        numberOfSubjects.setWidth("300px");
        TextField abnahmezahl = new TextField("Expected Number of Sample Deliveries");
        abnahmezahl.setWidth("300px");
        TextArea sender1 = new TextArea("Sender1");
        TextArea sender2 = new TextArea("Sender2");
        TextArea sender3 = new TextArea("Sender3");
        TextArea sponsor = new TextArea("Sponsor");
        TextField remark = new TextField("Remarks");
        DatePicker startdate = new DatePicker("Start Date");
        DatePicker enddate = new DatePicker("End Date");
        startdate.setPlaceholder("Start Date");
        startdate.setValue(LocalDate.now());
        enddate.setPlaceholder("End Date");
        enddate.setValue(LocalDate.now());
        //todo: add more metadatat

        Button save = new Button("Save");
        save.addClickListener(e->{
            //save study to database
            saveStudy(studyname.getValue(),startdate.getValue(),enddate.getValue(),numberOfSubjects.getValue(),
                    abnahmezahl.getValue(), sender1.getValue(), sender2.getValue(),
                    sender3.getValue(), sponsor.getValue(), remark.getValue());
            // navigate to study view
            save.getUI().ifPresent(ui -> ui.navigate("Studies"));
        });
        Component gridLayout = DISPLAY_UTILS.getBoxAlignment(3./4,studyname,startdate,enddate,numberOfSubjects,abnahmezahl,sender1,sender2,sender3,sponsor,remark);
        vl.add(gridLayout,save);
        add(vl);
    }

    private void saveStudy(String studyname, LocalDate startdate,LocalDate enddate,String numberOfSubjects,
                           String abnahmeZahl,
                           String sender1,String sender2,String sender3,String sponsor,
                           String remarks) {
        //save study to database
        if (studyRepository.existsByStudyName(studyname)) {
            Notification.show("Study Name already exists. Please choose another Name.");
            return;
        }
        Study study = createStudy(studyname, startdate, enddate, numberOfSubjects, abnahmeZahl, sender1, sender2, sender3, sponsor, remarks);
        studyRepository.save(study);
        Notification.show("Study saved successfully");
    }

    private Study createStudy(String studyname, LocalDate startdate, LocalDate enddate, String numberOfSubjects, String abnahmeZahl, String sender1, String sender2, String sender3, String sponsor, String remarks) {
        Study study = new Study();
        study.setStudyName(studyname);
        study.setStartDate(GENERAL_UTIL.convertToDate(startdate));
        study.setEndDate(GENERAL_UTIL.convertToDate(enddate));
        study.setExpectedNumberOfSampeDeliveries(abnahmeZahl);
        study.setRemark(remarks);
        study.setSponsor(sponsor);
        study.setSender1(sender1);
        study.setSender2(sender2);
        study.setSender3(sender3);
        study.setExpectedNumberOfSubjects(numberOfSubjects);
        return study;
    }


}
