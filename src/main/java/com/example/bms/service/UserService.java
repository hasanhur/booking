package com.example.bms.service;

import com.example.bms.entity.Role;
import com.example.bms.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User saveUser(User user);
    User getUserById(Long id);
    User getUser(String username);
    User updateUser(User user);
    void addRole(String username, String roleName);
    void deleteUserById(Long id);
    List<Role> getAllRoles();
    Role saveRole(Role role);
    Role getRoleById(Long id);
    Role getRole(String name);
    Role updateRole(Role role);
    void deleteRoleById(Long id);
}
