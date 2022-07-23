package com.example.bms.controller;

import com.example.bms.entity.*;
import com.example.bms.service.AppointmentService;
import com.example.bms.service.AppserviceService;
import com.example.bms.service.ShopService;
import com.example.bms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// This page is not complete yet
@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private AppointmentService appointmentService;
    private AppserviceService appserviceService;
    private ShopService shopService;

    public AdminController(UserService userService, AppointmentService appointmentService, AppserviceService appserviceService, ShopService shopService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.appserviceService = appserviceService;
        this.shopService = shopService;
    }

    @GetMapping("")
    public String showAdminPanel(Model model) {
        return "admin/home_page";
    }

    @GetMapping("/appointment")
    public String showAppointmentPanel(Model model, @RequestParam(name = "filter", defaultValue = "") String filter) {
        List<Appointment> appointments = this.appointmentService.getAllAppointments();

        // Get all expired appointments with no action so that we can cancel them
        switch (filter) {
            case "expired":
                appointments = appointments
                        .stream()
                        .filter(a -> a.getStatus().equals(Status.Created))
                        .filter(a -> a.getDate().getTime() < (Timestamp.from(Instant.now()).getTime()))
                        .collect(Collectors.toList());
                break;
            case "thisWeek":
                appointments = appointments
                        .stream()
                        .filter(a -> a.getDate().getTime() > (Timestamp.from(Instant.now()).getTime()))
                        .filter(a -> a.getDate().getTime() <= (Timestamp.from(Instant.now()).getTime() + 7 * 24 * 60 * 60 * 1000))
                        .collect(Collectors.toList());
                break;
            default:
                // do nothing
                break;
        }

        model.addAttribute("appointments", appointments);
        return "admin/appointment";
    }

    @GetMapping("/appservice")
    public String showAppservicePanel(Model model,
                                      @RequestParam(name = "free", defaultValue = "false") String free,
                                      @RequestParam(name = "distinct", defaultValue = "false") String distinct,
                                      @RequestParam(name = "query", defaultValue = "") String query,
                                      @RequestParam(name = "limit", defaultValue = "20") String limit,
                                      @RequestParam(name = "skip", defaultValue = "0") String skip,
                                      @RequestParam(name = "minPrice", defaultValue = "0") String minPrice) {
        List<Appservice> appservices = this.appserviceService.getAllAppservices();

        if (free == "true") {
            // Get services which costs 0
            appservices = appservices.stream()
                    .filter(appservice -> appservice.getPrice().compareTo(BigDecimal.valueOf(0)) == 0)
                    .collect(Collectors.toList());
        }

        if (distinct == "true") {
            // Get services a with distinct names
            appservices = appservices.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Get services with price smaller or equals to minPrice input and map their prices to int
        appservices = appservices.stream()
                .filter(appservice -> appservice.getPrice().compareTo(BigDecimal.valueOf(Long.parseLong(minPrice))) >= 0)
                .collect(Collectors.toList());

        // Get services which contains the search query
        appservices = appservices.stream()
                .filter(appservice -> appservice.getName().contains(query))
                .collect(Collectors.toList());

        // Limit the number of services returned
        appservices = appservices.stream()
                .skip(Long.parseLong(skip))
                .limit(Long.parseLong(limit))
                .collect(Collectors.toList());

        // Get services with maximum and minimum prices and return an array containing the elements
        Appservice mostExpensiveAppservice = appservices.stream()
                .max(Comparator.comparing(Appservice::getPrice)).get();

        Appservice cheapestAppservice = appservices.stream()
                .min(Comparator.comparing(Appservice::getPrice)).get();

        // Get the stream of array returned from the previous operation
        Stream<Appservice> appserviceStream = Stream.of(mostExpensiveAppservice, cheapestAppservice);

        // Map the cheapest and the most expensive services with their prices
        Map<String, BigDecimal> shopAppservices = appserviceStream
                .collect(Collectors.toMap(a -> a.getName(), a -> a.getPrice()));

        model.addAttribute("appservices", appservices);
        return "admin/appservice";
    }

    @GetMapping("/shop")
    public String showShopPanel(Model model, @RequestParam(value = "minServiceCount", defaultValue = "0") String minServiceCount) {
        List<Shop> shops = this.shopService.getAllShops();

        // Get shops with at least specified number of services
        shops = shops.stream()
                .filter(shop -> shop.getAppservices().size() >= Integer.parseInt(minServiceCount))
                .collect(Collectors.toList());

        // Get map of shops with their average service price
        Map<Shop, Double> averagePrices = shops.stream()
                .collect(Collectors.toMap(shop -> shop, shop -> shop.getAppservices().stream()
                        .mapToDouble(appservice -> appservice.getPrice().doubleValue())
                                .average().getAsDouble()
                        ));

        model.addAttribute("shops", shops);
        return "admin/shop";
    }

    @GetMapping("/user")
    public String showUserPanel(Model model) {
        List<User> users = this.userService.getAllUsers();

        boolean isFullNames = true;

        // Get full names of users
        List<String> fullNames = new ArrayList<>();
        users.forEach(user -> fullNames.add(user.getFirstName() + " " + user.getLastName()));

        // Get user first names sorted and capitalized
        fullNames.stream().sorted()
                .map(x->x.toUpperCase().split(" ")[0]).forEach(fullNames::add);

        // Get user first names that doesn't contain the letter a
        Predicate<String> nameFilter = x -> !x.toLowerCase().contains("a");
        fullNames.stream().filter(nameFilter).toArray();

        // Sort users by their id
        User[] usersSortedById = this.userService.getAllUsers().stream().sorted(Comparator.comparingLong(User::getId)).toArray(User[]::new);

        // Sort users by the length of their full name
        List<User> usersSortedByNameLength = this.userService.getAllUsers().stream().sorted(Comparator.comparingLong(User::getId)).collect(Collectors.toList());

        // Get all users whose all appointments are at least 7 days ago
        List<User> usersWithAppointmentWithinAWeek = this.userService.getAllUsers().stream()
                .filter(u -> u.getAppointments().stream()
                        .noneMatch(a -> a.getDate().getTime() > (Timestamp.from(Instant.now()).getTime() - 7 * 24 * 60 * 60 * 1000)))
                .collect(Collectors.toList());

        // Get users who have at least one appointment today using .toArray(User[]::new)
        List<User> usersWithAppointmentToday = this.userService.getAllUsers().stream()
                .filter(u -> u.getAppointments().stream()
                        .anyMatch(a -> a.getDate().toString().split(" ")[0] == Timestamp.from(Instant.now()).toString().split(" ")[0]))
                .collect(Collectors.toList());

        // Count the number of users without a first name
        Long count = this.userService.getAllUsers().stream()
                .filter(user -> user.getFirstName() == null)
                .count();

        // Create a list of usernames
        List<String> usernames = new ArrayList<>();
        users.stream().forEach(user -> usernames.add(user.getUsername()));
        model.addAttribute("usernames", usernames);

        model.addAttribute("fullNames", fullNames);
        model.addAttribute("isFullNames", isFullNames);
        return "admin/user";
    }

    @GetMapping("/role")
    public String showRolePanel(Model model, @RequestParam(name = "filter", defaultValue = "") String filter) {
        List<Role> roles = this.userService.getAllRoles();

        switch (filter) {
            case "withPrefix":
                roles = roles.stream().filter(role -> role.getName().startsWith("ROLE_")).collect(Collectors.toList());
            case "withoutPrefix":
                roles = roles.stream().filter(role -> !role.getName().startsWith("ROLE_")).collect(Collectors.toList());
        }

        // Check if every role is starting with ROLE_
        boolean hasPrefix = roles.stream().allMatch(role -> !role.getName().startsWith("ROLE_"));

        model.addAttribute("roles", roles);
        model.addAttribute("hasPrefix", hasPrefix);

        return "admin/role";
    }
}
