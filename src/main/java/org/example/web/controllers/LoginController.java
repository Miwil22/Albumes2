package org.example.web.controllers;

import org.example.rest.users.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    @GetMapping("/")
    public String welcome() {
        return "redirect:/public/";
    }

    @GetMapping("/auth/login")
    public String login(Model model) {
        // CSRF token es manejado globalmente
        model.addAttribute("usuario", new User());
        return "login"; // Plantilla: resources/templates/login.peb.html
    }
}