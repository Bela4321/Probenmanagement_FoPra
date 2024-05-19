package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
    @Getter
    @Setter
    @Entity
    @Table(name = "parameter")

    public class Parameter {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String parameterName;

        @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL)
        private List<Analysis> listOfAnalysis;

        public Parameter() {

        }

        public Parameter(Long Id, String parameterName) {
            this.id = id;
            this.parameterName = parameterName;
        }
}
