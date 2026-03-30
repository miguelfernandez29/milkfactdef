package com.example.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "Milk Management System");
        return "index";
    }

    @GetMapping("/menu")
    public String mainMenu(Model model) {
        model.addAttribute("appName", "Milk Management System");
        return "index";
    }
}