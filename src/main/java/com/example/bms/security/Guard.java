package com.example.bms.security;

import com.example.bms.entity.Appointment;
import com.example.bms.entity.Appservice;
import com.example.bms.entity.Shop;
import com.example.bms.service.AppointmentService;
import com.example.bms.service.AppserviceService;
import com.example.bms.service.ShopService;
import com.example.bms.service.UserService;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class Guard {
    private final AppointmentService appointmentService;
    private final AppserviceService appserviceService;
    private final ShopService shopService;
    private final UserService userService;

    public Guard(AppointmentService appointmentService, AppserviceService appserviceService, ShopService shopService, UserService userService) {
        this.appointmentService = appointmentService;
        this.appserviceService = appserviceService;
        this.shopService = shopService;
        this.userService = userService;
    }

    public Boolean checkAppointment(Principal principal, Long id, boolean isDeletable) {
        // If appointment doesn't exist, then return false
        if (null == appointmentService.getAppointmentById(id)) return false;

        Appointment appointment = appointmentService.getAppointmentById(id);

        // Check if the appointment was created by the current user
        boolean isCreator = appointment.getUser().getUsername().equals(principal.getName());

        // If this is a delete request, then authorise only the creator
        if (isDeletable) return isCreator;

        // Check if the current user owns the shop for which the appointment was created
        boolean isShopOwner = appointment.getAppservice().getShop().getUser().getUsername().equals(principal.getName());

        return isCreator || isShopOwner;
    }

    public Boolean checkAppservice(Principal principal, Long id) {
        Appservice appservice = appserviceService.getAppserviceById(id);
        return null != appservice && appservice.getShop().getUser().getUsername().equals(principal.getName());
    }

    public Boolean checkShop(Principal principal, Long id) {
        Shop shop = shopService.getShopById(id);
        return null != shop && shop.getUser().getUsername().equals(principal.getName());
    }

    public Boolean checkUser(Principal principal, Long id) {
        return userService.getUserById(id).getUsername().equals(principal.getName());
    }
}