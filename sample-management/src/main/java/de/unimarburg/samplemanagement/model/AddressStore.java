package de.unimarburg.samplemanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "address_store", uniqueConstraints = {
        @UniqueConstraint(columnNames = "address_key")
})
public class AddressStore {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String addressKey;

    private String address;

}
