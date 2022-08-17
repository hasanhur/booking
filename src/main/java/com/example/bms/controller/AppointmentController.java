package com.example.bms.controller;

import com.example.bms.entity.Appointment;
import com.example.bms.entity.Status;
import com.example.bms.service.AppointmentService;
import com.example.bms.service.AppserviceService;
import com.example.bms.service.ShopService;
import com.example.bms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {
    private AppointmentService appointmentService;
    private AppserviceService appserviceService;
    private UserService userService;
    private ShopService shopService;

    public AppointmentController(AppointmentService appointmentService, AppserviceService appserviceService, UserService userService, ShopService shopService) {
        super();
        this.appointmentService = appointmentService;
        this.appserviceService = appserviceService;
        this.userService = userService;
        this.shopService = shopService;
    }

    // handler method to handle list appointments and return model
    @GetMapping("")
    public String listAppointments(Model model, HttpServletRequest request, Principal principal) {
        List<Appointment> appointments = (request.isUserInRole("ROLE_ADMIN")) ? appointmentService.getAllAppointments() : userService.getUser(principal.getName()).getAppointments();
        model.addAttribute("appointments", appointments);
        model.addAttribute("isShopOwner", false);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "appointment/list";
    }

    @GetMapping("/{id}")
    public String showAppointment(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "appointment/show";
    }

    @GetMapping("/new/{id}")
    public String createAppointmentForm(@PathVariable Long id, Model model) {
        Appointment appointment = new Appointment();
        model.addAttribute("appointment", appointment);
        model.addAttribute("appservice", appserviceService.getAppserviceById(id));
        return "appointment/create";
    }

    @PostMapping("/service/{id}")
    public String saveAppointment(@PathVariable Long id, @RequestParam("time") String time, @ModelAttribute("appointment") Appointment appointment, RedirectAttributes redirectAttributes, Principal principal) {
        appointment.setAppservice(appserviceService.getAppserviceById(id));
        time += ":00";
        Timestamp appointmentTime = Timestamp.valueOf(time.replace("T", " "));
        String day = (new SimpleDateFormat("EEEE")).format(appointmentTime.getTime()).toUpperCase();
        LocalTime timeOfDay = LocalTime.parse((new SimpleDateFormat("HH:mm")).format(appointmentTime.getTime()));

        if (appointmentTime.before(Timestamp.from(Instant.now()))) {
            redirectAttributes.addFlashAttribute("alert", "Appointment can't be scheduled to the past!");
            return "redirect:/appointment/new/{id}";
        }

        if (!shopService.checkIfShopIsOpen(appointment.getAppservice().getShop().getId(), timeOfDay, day)) {
            redirectAttributes.addFlashAttribute("alert", "The shop is not open at the specified time. Please choose another date!");
            return "redirect:/appointment/new/{id}";
        }

        appointment.setDate(appointmentTime);
        appointment.setUser(userService.getUser(principal.getName()));
        appointmentService.saveAppointment(appointment);

        return "redirect:/appointment";
    }

    @GetMapping("/edit/{id}")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "appointment/edit";
    }

    @PutMapping("/{id}")
    public String updateAppointment(@PathVariable("id") Long id,
                                    Principal principal,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes,
                                    @RequestParam(name = "time", required = false) String time,
                                    @RequestParam(name = "status", required = false) String status) throws AccessDeniedException {

        Appointment appointment = appointmentService.getAppointmentById(id);
        final String username = principal.getName();
        final String shopOwnerUsername = appointment.getAppservice().getShop().getUser().getUsername();
        final String customerUsername = appointment.getUser().getUsername();

        // Make sure that customers can't change the status to anything other than cancelled and shops can't change the date
        if (!request.isUserInRole("ROLE_ADMIN")) {
            if ((null != time && !username.equals(customerUsername)) || (null != status && !status.equals("Cancelled") && !username.equals(shopOwnerUsername))) {
                throw new AccessDeniedException("Operation not permitted");
            }
        }

        if (null != time) {
            time += ":00";

            Timestamp appointmentTime = Timestamp.valueOf(time.replace("T", " "));
            String day = (new SimpleDateFormat("EEEE")).format(appointmentTime.getTime()).toUpperCase();
            LocalTime timeOfDay = LocalTime.parse((new SimpleDateFormat("HH:mm")).format(appointmentTime.getTime()));

            if (appointmentTime.before(Timestamp.from(Instant.now()))) {
                redirectAttributes.addFlashAttribute("alert", "Appointment can't be scheduled to the past!");
                return "redirect:/appointment/edit/{id}";
            }

            if (!shopService.checkIfShopIsOpen(appointment.getAppservice().getShop().getId(), timeOfDay, day)) {
                redirectAttributes.addFlashAttribute("alert", "The shop is not open at the specified time. Please choose another date!");
                return "redirect:/appointment/edit/{id}";
            }

            appointment.setDate(Timestamp.valueOf(time.replace("T", " ")));
            appointment.setStatus(Status.Created);
        }

        if (null != status) {
            appointment.setStatus(Status.valueOf(status));
            appointmentService.updateAppointment(appointment);
        }

        // update the appointment
        appointmentService.updateAppointment(appointment);
        return "redirect:/appointment";
    }

    @DeleteMapping("/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
        return "redirect:/appointment";
    }
}
