package com.example.bms.repository;

import com.example.bms.entity.Shop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShopRepositoryTest {

    @Autowired private ShopRepository shopRepository;

    @Test
    void injectedComponentsAreNotNull() {
        Assertions.assertNotNull(shopRepository);
    }

    @Test
    void anyShopExists( ) {
        Assertions.assertNotNull(shopRepository.findAll());
    }

    @Test
    void shopWithGivenIdExists() {
        Assertions.assertTrue(shopRepository.findById(8L).isPresent());
    }

    @Test
    void newShopSaved() {
        Shop shop = new Shop();
        shop.setName("New Barbershop");
        shop.setDescription("Some stuff that barbershops do");
        shop.setImage("randomimagepath");
        shop.setUser(shopRepository.findById(8L).get().getUser());
        Assertions.assertEquals(shopRepository.save(shop), shop);
    }

    @Test
    void shopUpdated() {
        Shop shop = shopRepository.findById(8L).get();

        String expectedDescription = "We sell bikes.";
        Assertions.assertEquals(shop.getDescription(), expectedDescription);

        String updatedDescription = "Rent a car from our store";
        shop.setDescription("Rent a car from our store");
        shopRepository.save(shop);

        Assertions.assertEquals(shopRepository.findById(8L).get().getDescription(), updatedDescription);
    }

    @Test
    void shopDeleted() {
        shopRepository.deleteById(7L);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            shopRepository.findById(7L).get();
        });
    }

}