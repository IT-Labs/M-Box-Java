package com.app.MBox.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/home" )
public class homeController {
    ;


    @GetMapping("/homepage")
    public String home() {
        return "home";

    }

    @RequestMapping(value = "/artists")
    public String showArtists() {
    return "artistsListPage";
    }

    @RequestMapping(value = "/record-labels")
    public String showRecordLabels() {
        return "recordLabelsListPage";
    }

    @RequestMapping(value = "/about")
    public String showAbout() {
        return "about";
    }



}
