package de.unimarburg.samplemanagement.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SubjectCompositeKey {
    private Long studyId;
    private Long id;


}
