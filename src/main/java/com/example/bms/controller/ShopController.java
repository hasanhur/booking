package com.example.bms.controller;

import com.example.bms.entity.BusinessHour;
import com.example.bms.entity.Day;
import com.example.bms.entity.Shop;
import com.example.bms.service.AppointmentService;
import com.example.bms.service.ShopService;
import com.example.bms.service.UserService;
import com.example.bms.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/shop")
public class ShopController {
    private ShopService shopService;
    private UserService userService;
    private AppointmentService appointmentService;

    public ShopController(ShopService shopService, UserService userService, AppointmentService appointmentService) {
        super();
        this.shopService = shopService;
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("")
    public String listShops(Model model, HttpServletRequest request) {
        boolean isActionable = request.isUserInRole("ROLE_ADMIN");
        model.addAttribute("isActionable", isActionable);
        model.addAttribute("shops", shopService.getAllShops());
        return "shop/list";
    }

    @GetMapping("/{id}")
    public String showShop(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request) {
        boolean isShopOwner = principal.getName().equals(shopService.getShopById(id).getUser().getUsername());
        boolean isActionable = request.isUserInRole("ROLE_ADMIN") || isShopOwner;
        model.addAttribute("isActionable", isActionable);
        Shop shop = shopService.getShopById(id);
        model.addAttribute("shop", shop);
        model.addAttribute("businessHours", shop.getBusinessHours());
        model.addAttribute("appointments", appointmentService.getAppointmentsByShopId(id));
        model.addAttribute("isShopOwner", isShopOwner);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "shop/show";
    }

    @GetMapping("/new")
    public String createShopForm(Model model) {
        Shop shop = new Shop();
        model.addAttribute("shop", shop);
        return "shop/create";
    }

    @PostMapping("")
    public String saveShop(@ModelAttribute("shop") Shop shop, @RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        // set image field
        String path = Utility.saveImage(file, "public/img/shop/", "jpg");
        shop.setImage(path);

        shop.setUser(userService.getUser(principal.getName()));
        shopService.saveShop(shop);
        return "redirect:/shop";
    }

    @GetMapping("/edit/{id}")
    public String editShopForm(@PathVariable Long id, Model model) {
        model.addAttribute("shop", shopService.getShopById(id));
        return "shop/edit";
    }

    @PutMapping("/{id}")
    public String updateShop(@PathVariable Long id, @ModelAttribute("shop") Shop shop, @RequestParam(name = "file", required = false) MultipartFile file, @RequestParam Map<String,String> requestParams) throws IOException {
        Shop existingShop = shopService.getShopById(id);
        // set image field
        if (!file.isEmpty()) {
            String path = Utility.saveImage(file, "public/img/shop/", "jpg");
            existingShop.setImage(path);
        }

        // set other fields from the form
        existingShop.setName(shop.getName());
        existingShop.setDescription(shop.getDescription());

        List<BusinessHour> businessHours = new ArrayList<>();

        // set opening and closing hours
        Stream.of(Day.values())
                .forEach(day -> {
                    if (requestParams.get(day.toString().toLowerCase() + "_open").isEmpty() || requestParams.get(day.toString().toLowerCase() + "_close").isEmpty()) {
                        return;
                    }
                    LocalTime openTime = LocalTime.parse(requestParams.get(day.toString().toLowerCase() + "_open"));
                    LocalTime closeTime = LocalTime.parse(requestParams.get(day.toString().toLowerCase() + "_close"));
                    BusinessHour businessHour = shopService.getBusinessHour(existingShop.getId(), day) != null ? shopService.getBusinessHour(existingShop.getId(), day) : new BusinessHour();
                    businessHour.setDay(day);
                    businessHour.setOpenTime(openTime);
                    businessHour.setCloseTime(closeTime);
                    businessHour.setShop(existingShop);
                    businessHours.add(businessHour);
                });

        existingShop.setBusinessHours(businessHours);

        // update the shop
        shopService.updateShop(existingShop);

        return "redirect:/shop";
    }

    @DeleteMapping("/{id}")
    public String deleteShop(@PathVariable Long id) {
        shopService.deleteShopById(id);
        return "redirect:/shop";
    }
}
