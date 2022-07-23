package com.example.bms.controller;

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
import java.nio.file.AccessDeniedException;
import java.security.Principal;

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

    @PostMapping("/new")
    public String saveShop(@ModelAttribute("shop") Shop shop, @RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        // set image field
        String path = Utility.saveImage(file, "public/img/shop/", "jpg");
        shop.setImage(path);

        shop.setUser(userService.getUser(principal.getName()));
        shopService.saveShop(shop);
        return "redirect:/shop";
    }

    @GetMapping("/edit/{id}")
    public String editShopForm(@PathVariable Long id, Model model, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        Shop shop = shopService.getShopById(id);
        if (!request.isUserInRole("ROLE_ADMIN") && !principal.getName().equals(shop.getUser().getUsername())) {
            log.error("Unauthorized user {} tried to view the edit form of shop id {}", principal.getName(), id);
            throw new AccessDeniedException("403");
        }
        model.addAttribute("shop", shopService.getShopById(id));
        return "shop/edit";
    }

    @PutMapping("/edit/{id}")
    public String updateShop(@PathVariable Long id, Model model, @ModelAttribute("shop") Shop shop, @RequestParam("file") MultipartFile file, HttpServletRequest request, Principal principal) throws IOException {
        // get shop from database by id
        Shop existingShop = shopService.getShopById(id);

        if (!request.isUserInRole("ROLE_ADMIN") && !principal.getName().equals(existingShop.getUser().getUsername())) {
            log.error("Unauthorized user {} sent request to update the shop id {}", principal.getName(), id);
            throw new AccessDeniedException("403");
        }
        // set image field
        String path = Utility.saveImage(file, "public/img/shop/", "jpg");
        existingShop.setImage(path);

        // set other fields from the form
        existingShop.setName(shop.getName());
        existingShop.setDescription(shop.getDescription());

        // update the shop
        shopService.updateShop(existingShop);
        return "redirect:/shop";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteShop(@PathVariable Long id, HttpServletRequest request, Principal principal) throws AccessDeniedException {
        if (!request.isUserInRole("ROLE_ADMIN") && !principal.getName().equals(shopService.getShopById(id).getUser().getUsername())) {
            log.error("Unauthorized user {} sent request to delete the shop id {}", principal.getName(), id);
            throw new AccessDeniedException("403");
        }
        shopService.deleteShopById(id);
        return "redirect:/shop";
    }
}
