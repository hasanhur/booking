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
        }

        // Sort by id descending so that the newest appointment will be at the top
        appointments = appointments.stream()
                .sorted(Comparator.comparingLong(Appointment::getId).reversed())
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointments);
        return "admin/appointment";
    }

    @GetMapping("/service")
    public String showAppservicePanel(Model model,
                                      @RequestParam(name = "free", defaultValue = "") String free,
                                      @RequestParam(name = "distinct", defaultValue = "") String distinct,
                                      @RequestParam(name = "query", defaultValue = "") String query,
                                      @RequestParam(name = "limit", defaultValue = "20") String limit,
                                      @RequestParam(name = "page", defaultValue = "1") String page,
                                      @RequestParam(name = "min_price", defaultValue = "0") Double minPrice) {
        List<Appservice> appservices = this.appserviceService.getAllAppservices();

        if (free.equals("on")) {
            // Get services which costs 0
            appservices = appservices.stream()
                    .filter(appservice -> appservice.getPrice().compareTo(BigDecimal.valueOf(0)) == 0)
                    .collect(Collectors.toList());
        }

        if (distinct.equals("on")) {
            // Get services a with distinct names
            appservices = appservices.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Get services with price smaller or equals to minPrice input and map their prices to int
        appservices = appservices.stream()
                .filter(appservice -> appservice.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0)
                .collect(Collectors.toList());

        // Get services which contains the search query
        appservices = appservices.stream()
                .filter(appservice -> appservice.getName().contains(query) ||
                        appservice.getDescription().contains(query))
                .collect(Collectors.toList());

        // Limit the number of services returned
        appservices = appservices.stream()
                .skip((Long.parseLong(page) -  1) * Long.parseLong(limit))
                .limit(Long.parseLong(limit))
                .collect(Collectors.toList());

        // Get services with maximum and minimum prices and return an array containing the elements
        if (!appservices.isEmpty()) {
            Appservice mostExpensiveAppservice = appservices.stream()
                    .distinct()
                    .max(Comparator.comparing(Appservice::getPrice)).get();

            Appservice cheapestAppservice = appservices.stream()
                    .distinct()
                    .min(Comparator.comparing(Appservice::getPrice)).get();

            if (!mostExpensiveAppservice.equals(cheapestAppservice)) {
                // Get the stream of array returned from the previous operation
                Stream<Appservice> appserviceStream = Stream.of(mostExpensiveAppservice, cheapestAppservice);

                // Map the cheapest and the most expensive services with their prices
                Map<String, Double> appServiceMap = appserviceStream
                        .collect(Collectors.toMap(Appservice::getName, a -> a.getPrice().doubleValue()));

                model.addAttribute("appserviceMap", appServiceMap);
            }
        }

        // Sort by id
        appservices = appservices.stream()
                .sorted(Comparator.comparingLong(Appservice::getId))
                .collect(Collectors.toList());

        model.addAttribute("appservices", appservices);
        return "admin/appservice";
    }

    @GetMapping("/shop")
    public String showShopPanel(Model model,
                                @RequestParam(value = "min_service_count", defaultValue = "0") String minServiceCount,
                                @RequestParam(value = "average_price", defaultValue = "") String averagePrice,
                                @RequestParam(name = "query", defaultValue = "") String query) {
        List<Shop> shops = this.shopService.getAllShops();

        // Filter the list by search query
        shops = shops.stream()
                .filter(shop -> shop.getName().contains(query) ||
                        shop.getDescription().contains(query))
                .collect(Collectors.toList());

        // Get shops with at least specified number of services
        shops = shops.stream()
                .filter(shop -> shop.getAppservices().size() >= Integer.parseInt(minServiceCount))
                .collect(Collectors.toList());

        if (averagePrice.equals("on")) {
            // Get map of shops with their average service price
            Map<Shop, Double> shopMap = shops.stream()
                    .collect(Collectors.toMap(
                            shop -> shop,
                            shop -> shop.getAppservices().stream()
                                    .mapToDouble(appservice -> appservice.getPrice().doubleValue())
                                    .summaryStatistics().getAverage()
                    ));

            shopMap = shopMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
            model.addAttribute("shopMap", shopMap);
        } else {
            // Sort the list
            shops = shops.stream()
                    .sorted(Comparator.comparingLong(Shop::getId))
                    .collect(Collectors.toList());

            model.addAttribute("shops", shops);
        }
        return "admin/shop";
    }

    @GetMapping("/user")
    public String showUserPanel(Model model,
                                @RequestParam(name = "query", defaultValue = "") String query,
                                @RequestParam(name = "type", defaultValue = "") String type,
                                @RequestParam(name = "status", defaultValue = "") String status,
                                @RequestParam(name = "sort_by", defaultValue = "") String sortBy,
                                @RequestParam(name = "empty_first_name_count", defaultValue = "") String emptyFirstNameCount,
                                @RequestParam(name = "limit", defaultValue = "10") String limit,
                                @RequestParam(name = "page", defaultValue = "1") String page) {
        List<User> users = this.userService.getAllUsers();

        switch (type) {
            case "full_name":
                // Get full names of users
                List<String> fullNames = new ArrayList<>();
                users.forEach(user -> fullNames.add(user.getFirstName().concat(" " + user.getLastName())));
                model.addAttribute("type", "Full Name");
                model.addAttribute("names", fullNames);
                break;
            case "first_name":
                List<String> firstNames = new ArrayList<>();

                // Get user first names that contains the search query
                Predicate<String> searchFilter = x -> x.contains(query);
                firstNames.stream().filter(searchFilter).toArray();

                // Get user first names sorted and capitalized
                firstNames.stream().sorted()
                        .map(x->x.toUpperCase().split(" ")[0]).forEach(firstNames::add);

                model.addAttribute("type", "First Name");
                model.addAttribute("names", firstNames);
                break;
            case "username":
                // Create a list of usernames
                List<String> usernames = new ArrayList<>();
                users.stream().forEach(user -> usernames.add(user.getUsername()));
                model.addAttribute("type", "Username");
                model.addAttribute("names", usernames);
                break;
            default:
                // Filter the list by search query
                users = users.stream()
                        .filter(user -> user.getUsername().contains(query) ||
                                user.getFirstName().contains(query) ||
                                user.getLastName().contains(query) ||
                                user.getRoles().contains(query) ||
                                user.getEmail().contains(query))
                        .collect(Collectors.toList());

                switch (sortBy) {
                    case "id":
                        // Sort users by their id ascending
                        users = users.stream().sorted(Comparator.comparingLong(User::getId)).collect(Collectors.toList());
                        break;
                    case "full_name_length":
                        // Sort users by the length of their full name ascending
                        users = users.stream()
                                .sorted(Comparator.comparingInt(user -> user.getFirstName().length() + user.getLastName().length()))
                                .collect(Collectors.toList());
                        break;
                    default:
                        // do nothing
                }

                switch (status) {
                    case "today":
                        // Get users who have at least one appointment today
                        users = users.stream()
                                .filter(u -> u.getAppointments().stream()
                                        .anyMatch(a -> a.getDate().toString().split(" ")[0].equals(Timestamp.from(Instant.now()).toString().split(" ")[0])))
                                .collect(Collectors.toList());
                        break;
                    case "none_last_week":
                        // Get all users whose all appointments are at least 7 days ago
                        users = users.stream()
                                .filter(u -> u.getAppointments().stream()
                                        .noneMatch(a -> a.getDate().getTime() > (Timestamp.from(Instant.now()).getTime() - 7 * 24 * 60 * 60 * 1000)))
                                .collect(Collectors.toList());
                        break;
                    default:
                        // do nothing
                }

                users = users.stream()
                        .skip((Long.parseLong(page) -  1) * Long.parseLong(limit))
                        .limit(Long.parseLong(limit))
                        .collect(Collectors.toList());

                model.addAttribute("users", users);
        }

        if (emptyFirstNameCount == "on") {
            // Count the number of users without a first name in the filtered list
            Long count = this.userService.getAllUsers().stream()
                    .filter(user -> user.getFirstName() == null)
                    .count();
            model.addAttribute("noFirstNameCount", emptyFirstNameCount);
        }

        return "admin/user";
    }

    @GetMapping("/role")
    public String showRolePanel(Model model, @RequestParam(name = "filter", defaultValue = "") String filter) {
        List<Role> roles = this.userService.getAllRoles();

        // Check if there is a user role
        boolean userRoleExists = roles.stream().anyMatch(role -> role.getName().replaceAll("^ROLE_", "").equals("USER"));

        switch (filter) {
            case "with_prefix":
                roles = roles.stream().filter(role -> role.getName().startsWith("ROLE_")).collect(Collectors.toList());
                break;
            case "without_prefix":
                roles = roles.stream().filter(role -> !role.getName().startsWith("ROLE_")).collect(Collectors.toList());
                break;
            default:
                // do nothing
        }

        // Check if every role is starting with ROLE_
        boolean hasPrefix = roles.stream().allMatch(role -> role.getName().startsWith("ROLE_"));

        model.addAttribute("roles", roles);
        model.addAttribute("hasPrefix", hasPrefix);
        model.addAttribute("userRoleExists", userRoleExists);

        return "admin/role";
    }
}
