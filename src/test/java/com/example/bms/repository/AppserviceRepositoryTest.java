package com.example.bms.repository;

import com.example.bms.entity.Appservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppserviceRepositoryTest {

    @Autowired private AppserviceRepository appserviceRepository;
    @Autowired private ShopRepository shopRepository;

    @Test
    void anyAppserviceExists( ) {
        Assertions.assertNotNull(appserviceRepository.findAll());
    }

    @Test
    void appserviceWithGivenIdExists() {
        Assertions.assertTrue(appserviceRepository.findById(3L).isPresent());
    }

    @Test
    void newAppserviceSaved() {
        Appservice appservice = new Appservice();
        appservice.setName("Women haircut");
        appservice.setDescription("Haircut for women");
        appservice.setShop(shopRepository.findById(8L).get());
        appservice.setPrice(BigDecimal.valueOf(14.99));
        Assertions.assertEquals(appserviceRepository.save(appservice), appservice);
    }

    @Test
    void appserviceUpdated() {
        Appservice appservice = appserviceRepository.findById(3L).get();

        String expectedName = "some haircut";
        Assertions.assertEquals(appservice.getName(), expectedName);

        String updatedName = "Dental Service";
        appservice.setName("Dental Service");
        appserviceRepository.save(appservice);

        Assertions.assertEquals(appserviceRepository.findById(3L).get().getName(), updatedName);
    }

    @Test
    void appserviceDeleted() {
        appserviceRepository.deleteById(3L);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            appserviceRepository.findById(3L).get();
        });
    }
}