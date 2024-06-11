package de.unimarburg.samplemanagement.UI.create_study;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.unimarburg.samplemanagement.model.Study;
import de.unimarburg.samplemanagement.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class StudyForm extends VerticalLayout {
    @Autowired
    private StudyRepository studyRepository;
    public StudyForm() {
        TextField name = new TextField("Name");
        DatePicker startDate = new DatePicker("Start Date");
        startDate.setValue(LocalDate.now());
        add(name, startDate);
        Button save = new Button("Save");
        save.addClickListener(e -> {
            System.out.println("Name: " + name.getValue());
            System.out.println("Start Date: " + startDate.getValue());
            createStudy(name.getValue(), startDate.getValue());
        });
        add(save);
    }

    private void createStudy(String value, LocalDate value1) {
        //check name uniqueness
        if (studyRepository.existsByStudyName(value)) {
            Notification.show("Name already exists, please choose another name");
            return;
        }
        //save study
        Study study = new Study();
        study.setStudyName(value);
        study.setStudyDate(convertToDate(value1));
        studyRepository.save(study);

    }


    private static Date convertToDate(LocalDate localDate) {
        // Convert LocalDate to an Instant
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        // Create a Date from the Instant
        return Date.from(instant);
    }
}
