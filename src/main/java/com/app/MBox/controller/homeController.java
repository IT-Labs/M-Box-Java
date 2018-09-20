package com.app.MBox.controller;



import com.app.MBox.core.model.artist;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.dto.songDto;
import com.app.MBox.services.artistService;
import com.app.MBox.services.recordLabelService;
import com.app.MBox.services.songService;
import com.app.MBox.services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
@RequestMapping(value = "/home" )
public class homeController {
    @Autowired
    songService songService;
    @Autowired
    artistService artistService;
    @Autowired
    recordLabelService recordLabelService;
    @Autowired
    userService userService;

    @GetMapping("/homepage")
    public ModelAndView home(ModelAndView modelAndView, Model model) {
        List<songDto> songs;
        songs=songService.getMostRecentSongs();
        List<artistDto>artists;
        artists=artistService.findRecentlyAddedArtist();
        model.addAttribute("artists",artists);
        model.addAttribute("songs",songs);
        modelAndView.setViewName("home");
        return modelAndView;

    }

    @RequestMapping(value = "/artists")
    public ModelAndView showArtists(ModelAndView modelAndView,Model model) {
        modelAndView.setViewName("artistsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/record-labels")
    public ModelAndView showRecordLabels(ModelAndView modelAndView,Model model) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(0,25);
        model.addAttribute("recordLabels",recordLabels);
        modelAndView.setViewName("recordLabelsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/about")
    public String showAbout() {
        return "about";
    }



}
