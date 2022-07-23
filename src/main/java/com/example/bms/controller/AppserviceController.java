package com.example.bms.controller;

import com.example.bms.entity.Appservice;
import com.example.bms.entity.Shop;
import com.example.bms.service.AppserviceService;
import com.example.bms.service.ShopService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
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
    public String showAppservice(Appservice appservice, Model model, HttpServletRequest request, Principal principal) {
        boolean isShopOwner = principal.getName().equals(appservice.getShop().getUser().getUsername());
        boolean isActionable = request.isUserInRole("ROLE_ADMIN") || isShopOwner;
        model.addAttribute("appservice", appservice);
        model.addAttribute("isActionable", isActionable);
        model.addAttribute("isShopOwner", isShopOwner);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "appservice/show";
    }

    @GetMapping("/new/{id}")
    public String createAppserviceForm(Shop shop, Model model, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !shop.getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        Appservice appservice = new Appservice();
        model.addAttribute("appservice", appservice);
        model.addAttribute("shop", shop);
        return "appservice/create";
    }

    @PostMapping("/new/{id}")
    public String saveAppservice(Shop shop, @ModelAttribute("appservice") Appservice appservice, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !shop.getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        appservice.setShop(shop);
        appserviceService.saveAppservice(appservice);
        return "redirect:/service";
    }

    @GetMapping("/edit/{id}")
    public String editAppserviceForm(Appservice appservice, Model model, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appservice.getShop().getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        model.addAttribute("appservice", appservice);
        return "appservice/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateAppservice(@PathVariable Long id, Model model, @ModelAttribute("appservice") Appservice appservice, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appserviceService.getAppserviceById(id).getShop().getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
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

    @DeleteMapping("/delete/{id}")
    public String deleteAppservice(@PathVariable Long id, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !appserviceService.getAppserviceById(id).getShop().getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("403");
        }
        appserviceService.deleteAppserviceById(id);
        return "redirect:/service";
    }
}
