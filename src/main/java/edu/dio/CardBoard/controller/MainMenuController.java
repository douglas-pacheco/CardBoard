package edu.dio.CardBoard.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainMenuController {

    /**
     * Maps the GET request to the application root (or to /menu).
     * Returns the name of the Thymeleaf template to be rendered.
     */
    @GetMapping("/mainmenu")
    public String showMainMenu() {
        return "main-menu";
    }
}