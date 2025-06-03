package edu.dio.CardBoard.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(@RequestParam(name = "name", required = false, defaultValue = "visitor") String name, Model model) {
        model.addAttribute("name", name);

        model.addAttribute("showMessage", true);

        return "hello";
    }
}