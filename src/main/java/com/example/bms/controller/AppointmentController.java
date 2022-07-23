package com.example.bms.controller;

import com.example.bms.entity.Appointment;
import com.example.bms.entity.Status;
import com.example.bms.service.AppointmentService;
import com.example.bms.service.AppserviceService;
import com.example.bms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {
    private AppointmentService appointmentService;
    private AppserviceService appserviceService;
    private UserService userService;

    public AppointmentController(AppointmentService appointmentService, AppserviceService appserviceService, UserService userService) {
        super();
        this.appointmentService = appointmentService;
        this.appserviceService = appserviceService;
        this.userService = userService;
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
    public String showAppointment(@PathVariable Long id, Model model, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appointmentService.getAppointmentById(id).getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "appointment/show";
    }

    @GetMapping("/new/{id}")
    public String createAppointmentForm(@PathVariable Long id, Model model, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appserviceService.getAppserviceById(id).getShop().getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        Appointment appointment = new Appointment();
        model.addAttribute("appointment", appointment);
        model.addAttribute("appservice", appserviceService.getAppserviceById(id));
        return "appointment/create";
    }

    @PostMapping("/new/{id}")
    public String saveAppointment(@PathVariable Long id, @RequestParam("time") String time, @ModelAttribute("appointment") Appointment appointment, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appserviceService.getAppserviceById(id).getShop().getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        appointment.setAppservice(appserviceService.getAppserviceById(id));
        appointment.setUser(userService.getUser(principal.getName()));
        time += ":00";
        appointment.setDate(Timestamp.valueOf(time.replace("T", " ")));
        appointmentService.saveAppointment(appointment);
        return "redirect:/appointment";
    }

    @GetMapping("/edit/{id}")
    public String editAppointmentForm(@PathVariable Long id, Model model, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appointmentService.getAppointmentById(id).getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "appointment/edit";
    }

    @PutMapping("/edit/{id}")
    public String updateAppointment(@PathVariable Long id, Model model, @RequestParam("time") String time, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appointmentService.getAppointmentById(id).getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        // get appointment from database by id
        Appointment existingAppointment = appointmentService.getAppointmentById(id);

        // set the field from the form
        time += ":00";
        existingAppointment.setDate(Timestamp.valueOf(time.replace("T", " ")));
        existingAppointment.setStatus(Status.Created);

        // update the appointment
        appointmentService.updateAppointment(existingAppointment);
        return "redirect:/appointment";
    }

    @PutMapping("/update/{id}")
    public String changeStatus(@PathVariable Long id, @RequestParam("status") String status, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appointmentService.getAppointmentById(id).getAppservice().getShop().getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        Appointment appointment = appointmentService.getAppointmentById(id);
        appointment.setStatus(Status.valueOf(status));
        appointmentService.updateAppointment(appointment);
        return "redirect:/shop/" + appointment.getAppservice().getShop().getId();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appointmentService.getAppointmentById(id).getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        appointmentService.deleteAppointmentById(id);
        return "redirect:/appointment";
    }
}
