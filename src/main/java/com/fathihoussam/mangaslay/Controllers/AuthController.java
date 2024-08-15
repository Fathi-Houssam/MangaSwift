package com.fathihoussam.mangaslay.Controllers;

import com.fathihoussam.mangaslay.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    // Handle signup form submission
    @PostMapping("/signup")
    public String signup(@RequestParam("username") String username,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password,
                         Model model) {
        try {
            userService.registerUser(username, email, password);
            return "redirect:/"; // Redirect to home page after successful signup
        } catch (Exception e) {
            model.addAttribute("error", "Error during signup: " + e.getMessage());
            return "IndexPage"; // Return to the same page with error message
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) {
        try {
            userService.loginUser(username, password);

            // Set session ID after successful login
            session.setAttribute("sessionId", session.getId());
            session.setAttribute("username", username);

            return "redirect:/"; // Redirect to root after successful login
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "IndexPage"; // Show error message on the same page
        }
    }


}
