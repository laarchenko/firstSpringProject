package com.example.sweater.controller;
import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;

    public static UserDetails currentUserDetails(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            return principal instanceof UserDetails ? (UserDetails) principal : null;
        }
        return null;
    }

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        User user = (User) currentUserDetails();
        if(user != null) {
            model.put("name", user.getUsername());
            model.put("isAdmin", user.isAdmin());
        }
        else {
            model.put("name", "anonymous");
            model.put("isAdmin", false);
        }
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
        User user = (User) currentUserDetails();
        if(user != null) {
            model.put("name", user.getUsername());
            model.put("isAdmin", user.isAdmin());
        }
        else {
            model.put("name", "anonymous");
            model.put("isAdmin", false);
        }
        model.put("messages", messages);

        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Map<String, Object> model
            ) throws IOException {
        Message message = new Message(text, tag, user);

        messageRepo.save(message);

        Iterable<Message> messages = messageRepo.findAll();

        model.put("messages", messages);

        if(user != null) {
            model.put("name", user.getUsername());
            model.put("isAdmin", user.isAdmin());
        }
        else {
            model.put("name", "anonymous");
            model.put("isAdmin", false);
        }
        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter, Map<String, Object> model) {
        Iterable<Message> messages;
        User user = (User) currentUserDetails();
        if(user != null) {
            model.put("name", user.getUsername());
            model.put("isAdmin", user.isAdmin());
        }
        else {
            model.put("name", "anonymous");
            model.put("isAdmin", false);
        }
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }

        model.put("messages", messages);

        return "main";
    }
}