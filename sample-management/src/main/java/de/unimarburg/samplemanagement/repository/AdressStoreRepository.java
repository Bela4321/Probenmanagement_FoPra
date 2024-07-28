package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.AddressStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdressStoreRepository extends JpaRepository<AddressStore, Long> {
    Optional<AddressStore> findAddressStoreByAddressKey(String addressKey);
}
