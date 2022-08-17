package com.example.bms.controller;

import com.example.bms.entity.Appservice;
import com.example.bms.service.AppserviceService;
import com.example.bms.service.ShopService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/service")
public class AppserviceController {
    private AppserviceService appserviceService;
    private ShopService shopService;

    public AppserviceController(AppserviceService appserviceService, ShopService shopService) {
        super();
        this.appserviceService = appserviceService;
        this.shopService = shopService;
    }

    @GetMapping("")
    public String listAppservices(Model model, HttpServletRequest request) {
        model.addAttribute("appservices", appserviceService.getAllAppservices());
        model.addAttribute("isActionable", request.isUserInRole("ROLE_ADMIN"));
        return "appservice/list";
    }

    @GetMapping("/{id}")
    public String showAppservice(@PathVariable Long id, Model model, HttpServletRequest request, Principal principal) {
        Appservice appservice = appserviceService.getAppserviceById(id);
        boolean isShopOwner = principal.getName().equals(appservice.getShop().getUser().getUsername());
        boolean isActionable = request.isUserInRole("ROLE_ADMIN") || isShopOwner;
        model.addAttribute("appservice", appservice);
        model.addAttribute("isActionable", isActionable);
        model.addAttribute("isShopOwner", isShopOwner);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "appservice/show";
    }

    @GetMapping("/new/{id}")
    public String createAppserviceForm(@PathVariable Long id, Model model) {
        Appservice appservice = new Appservice();
        model.addAttribute("appservice", appservice);
        model.addAttribute("shop", shopService.getShopById(id));
        return "appservice/create";
    }

    @PostMapping("/shop/{id}")
    public String saveAppservice(@PathVariable Long id, @ModelAttribute("appservice") Appservice appservice) {
        appservice.setShop(shopService.getShopById(id));
        appserviceService.saveAppservice(appservice);
        return "redirect:/service";
    }

    @GetMapping("/edit/{id}")
    public String editAppserviceForm(@PathVariable Long id, Model model) {
        model.addAttribute("appservice", appserviceService.getAppserviceById(id));
        return "appservice/edit";
    }

    @PutMapping("/{id}")
    public String updateAppservice(@PathVariable Long id, @ModelAttribute("appservice") Appservice appservice) {
        // get appservice from database by id
        Appservice existingAppservice = appserviceService.getAppserviceById(id);

        // set fields from the form
        existingAppservice.setName(appservice.getName());
        existingAppservice.setDescription(appservice.getDescription());
        existingAppservice.setPrice(appservice.getPrice());

        // update the appservice
        appserviceService.updateAppservice(existingAppservice);
        return "redirect:/service";
    }

    @DeleteMapping("/{id}")
    public String deleteAppservice(@PathVariable Long id) {
        appserviceService.deleteAppserviceById(id);
        return "redirect:/service";
    }
}
