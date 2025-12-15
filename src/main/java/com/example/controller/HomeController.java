package com.example.controller;


import com.example.repo.UserRepo;
import com.example.model.User;
import com.example.service.JwtUtil;
import com.example.service.OnlineUserTracker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OnlineUserTracker onlineUserTracker;

    private BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if(token != null && jwtUtil.extractUsername(token) != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if(token != null && jwtUtil.extractUsername(token) != null) {
            return "redirect:/home";
        }
        return "register";
    }

    @PostMapping("/saveuser")
    public String saveUser(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpServletResponse response) {

        User user = userRepo.findByUsername(username).orElse(null);
        if(user == null || !passwordEncoder.matches(password, user.getHashedPassword())) {
            return "redirect:/login?error=true";
        }

        String token = jwtUtil.generateToken(username);
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(HttpServletRequest request, Model model) {
        String token = getTokenFromCookie(request);
        if(token == null || jwtUtil.extractUsername(token) == null) {
            return "redirect:/login";
        }

        String username = jwtUtil.extractUsername(token);

        onlineUserTracker.userLoggedIn(username);
        model.addAttribute("username", username);

        List<User> otherUsers = userRepo.findAll().stream()
                .filter(u -> !u.getUsername().equals(username))
                .collect(Collectors.toList());
        model.addAttribute("otherUsers", otherUsers);

        return "home";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response) {
        String token = getTokenFromCookie(request);
        if(token == null || jwtUtil.extractUsername(token) == null) {
            return "redirect:/login";
        }

        String username = jwtUtil.extractUsername(token);
        onlineUserTracker.userLoggedOut(username);
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/login?logout=true";
    }


    private String getTokenFromCookie(HttpServletRequest request) {
        if(request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("JWT_TOKEN"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
