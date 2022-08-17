package com.example.bms.service.impl;

import com.example.bms.entity.User;
import com.example.bms.entity.Role;
import com.example.bms.repository.RoleRepository;
import com.example.bms.repository.UserRepository;
import com.example.bms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users from the database");
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        log.info("Fetching user by id {}", id);
        return userRepository.findById(id).get(); // findById() returns Optional so we need to call get() function here
    }

    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(User user) {
        log.info("Updating user {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public void addRole(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Deleting user by id {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public List<Role> getAllRoles() {
        log.info("Fetching all roles from the database");
        return roleRepository.findAll();
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleById(Long id) {
        log.info("Fetching role by id {}", id);
        return roleRepository.findById(id).get();
    }

    @Override
    public Role getRole(String name) {
        log.info("Fetching role {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    public Role updateRole(Role role) {
        log.info("Updating role {}", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRoleById(Long id) {
        log.info("Deleting role by id {}", id);
        roleRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found in the database");
        } else {

        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
