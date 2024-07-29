package de.unimarburg.samplemanagement.repository;

import de.unimarburg.samplemanagement.model.AddressStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressStoreRepository extends JpaRepository<AddressStore, Long> {

    static final String ownAddressKey = "OUR_ADDRESS";

    Optional<AddressStore> findAddressStoreByAddressKey(String addressKey);

    default String getOwnAddress() {
        Optional<AddressStore> optionalAddressStore = findAddressStoreByAddressKey(ownAddressKey);
        if (optionalAddressStore.isPresent()) {
            return optionalAddressStore.get().getAddress();
        } else {
            return "";
        }
    }

    default void setOwnAddress(String address) {
        Optional<AddressStore> optionalAddressStore = findAddressStoreByAddressKey(ownAddressKey);
        AddressStore addressStore;
        if (optionalAddressStore.isPresent()) {
            addressStore = optionalAddressStore.get();
        } else {
            addressStore = new AddressStore();
            addressStore.setAddressKey(ownAddressKey);
        }
        addressStore.setAddress(address);
        save(addressStore);
    }
}
