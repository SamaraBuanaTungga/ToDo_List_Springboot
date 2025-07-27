package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

   @PostMapping("/register")
public String processRegister(@ModelAttribute User user, Model model) {
    if (userRepo.existsByUsername(user.getUsername())) {
        model.addAttribute("errorMessage", "Username sudah digunakan.");
        model.addAttribute("user", user);
        return "register";
    }

    if (userRepo.existsByEmail(user.getEmail())) {
        model.addAttribute("errorMessage", "Email sudah digunakan.");
        model.addAttribute("user", user);
        return "register";
    }

    user.setPassword(encoder.encode(user.getPassword()));
    userRepo.save(user);

    model.addAttribute("successMessage", "Pendaftaran berhasil. Silakan login.");
    return "redirect:/login";
}

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

