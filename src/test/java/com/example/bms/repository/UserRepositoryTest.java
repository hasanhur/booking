package com.example.bms.repository;

import com.example.bms.entity.Role;
import com.example.bms.entity.User;
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
class UserRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    @Test
    void findByUsername() {
        Assertions.assertEquals(userRepository.findByUsername("test").getId(), 5L);
    }

    @Test
    void anyUserExists( ) {
        Assertions.assertNotNull(userRepository.findAll());
    }

    @Test
    void userWithGivenIdExists() {
        Assertions.assertTrue(userRepository.findById(6L).isPresent());
    }

    @Test
    void newUserSaved() {
        User user = new User();

        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("ROLE_USER"));

        user.setUsername("newuser");
        user.setFirstName("Hannah");
        user.setLastName("Johnson");
        user.setPassword("password");
        user.setEmail("testdbuser@example.com");
        user.setRoles(roles);
        Assertions.assertEquals(userRepository.save(user), user);
    }

    @Test
    void userUpdated() {
        User user = userRepository.findById(8L).get();

        String expectedFirstName = "jack";
        Assertions.assertEquals(user.getFirstName(), expectedFirstName);

        String updatedFirstName = "john";
        user.setFirstName("john");
        userRepository.save(user);

        Assertions.assertEquals(userRepository.findById(8L).get().getFirstName(), updatedFirstName);
    }

    @Test
    void userDeleted() {
        userRepository.deleteById(7L);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            userRepository.findById(7L).get();
        });
    }
}