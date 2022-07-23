package com.example.bms.repository;

import com.example.bms.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired RoleRepository roleRepository;

    @Test
    void anyRoleExists( ) {
        Assertions.assertNotNull(roleRepository.findAll());
    }

    @Test
    void roleWithGivenIdExists() {
        Assertions.assertTrue(roleRepository.findById(6L).isPresent());
    }

    @Test
    void newRoleSaved() {
        Role role = new Role();
        role.setName("ROLE_NEWBIE");
        Assertions.assertEquals(roleRepository.save(role), role);
    }

    @Test
    void roleUpdated() {
        Role role = roleRepository.findById(7L).get();

        String expectedName = "ROLE_ADMIN";
        Assertions.assertEquals(role.getName(), expectedName);

        String updatedName = "ROLE_TEST";
        role.setName("ROLE_TEST");
        roleRepository.save(role);

        Assertions.assertEquals(roleRepository.findById(7L).get().getName(), updatedName);
    }

    @Test
    void roleDeleted() {
        roleRepository.deleteById(7L);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            roleRepository.findById(7L).get();
        });
    }
}