package com.example.bms.controller;

import com.example.bms.entity.Role;
import com.example.bms.entity.User;
import com.example.bms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Slf4j
@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        super();
        this.userService = userService;
    }

    @GetMapping("")
    public String showHomePage() {
        return "home_page";
    }

    @PostMapping("/logout")
    public void logout() {
    }

    @GetMapping("/login")
    public String login() {
        return "security/login";
    }

    // handler method to handle list users and return model
    @GetMapping("/user")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }

    @GetMapping("/profile")
    public String showUserProfile(Principal principal, Model model, HttpServletRequest request) {
        model.addAttribute("user", userService.getUserById(userService.getUser(principal.getName()).getId()));
        model.addAttribute("isActionable", request.isUserInRole("ROLE_ADMIN"));
        return showUser(userService.getUser(principal.getName()).getId(), model, request);
    }

    @GetMapping("/user/{id}")
    public String showUser(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("isActionable", request.isUserInRole("ROLE_ADMIN"));
        return "user/show";
    }

    @GetMapping("/register")
    public String createUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "user/create";
    }

    @PostMapping("/register")
    public String saveUser(@ModelAttribute("user") User user) {
        List<Role> roles = new ArrayList<>();
        roles.add(userService.getRole("ROLE_USER"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/user";
    }

    @GetMapping("/user/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("user", userService.getUserById(id));
        if (request.isUserInRole("ROLE_ADMIN")) {
            model.addAttribute("roles", userService.getAllRoles());
        }
        return "user/edit";
    }

    @PutMapping("/user/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, @ModelAttribute("roles") ArrayList<Role> roles, HttpServletRequest request) {
        // get user from database by id
        User existingUser = userService.getUserById(id);

        // set fields from the form
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        if (request.isUserInRole("ROLE_ADMIN")) {
            existingUser.setRoles(user.getRoles());
        }

        // update the user
        userService.updateUser(existingUser);
        return "redirect:/user";
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/user";
    }

    // Roles
    @GetMapping("/role")
    public String listRoles(Model model) {
        model.addAttribute("roles", userService.getAllRoles());
        return "role/list";
    }

    @GetMapping("/role/new")
    public String createRoleForm(Model model) {
        Role role = new Role();
        model.addAttribute("role", role);
        return "role/create";
    }

    @PostMapping("/role")
    public String saveRole(@ModelAttribute("role") Role role) {
        userService.saveRole(role);
        return "redirect:/role";
    }

    @GetMapping("/role/edit/{id}")
    public String editRoleForm(@PathVariable Long id, Model model) {
        model.addAttribute("role", userService.getRoleById(id));
        return "role/edit";
    }

    @PutMapping("/role/{id}")
    public String updateRole(@PathVariable Long id, @ModelAttribute("role") Role role) {
        // get role from database by id
        Role existingRole = userService.getRoleById(id);
        existingRole.setName(role.getName());

        userService.updateRole(existingRole);
        return "redirect:/role";
    }

    @DeleteMapping("/role/{id}")
    public String deleteRole(@PathVariable Long id) {
        userService.deleteRoleById(id);
        return "redirect:/role";
    }
}
