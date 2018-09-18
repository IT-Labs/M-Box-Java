package com.app.MBox.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/home" )
public class homeController {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/homepage")
    public String home() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities() + " authorityy");
        return "home";

    }

    @RequestMapping(value = "/artists")
    public String showArtists() {
    return "artistsListPage";
    }

    @RequestMapping(value = "/recordLabels")
    public String showRecordLabels() {
        return "recordLabelsListPage";
    }

    @RequestMapping(value = "/about")
    public String showAbout() {
        return "about";
    }



}
