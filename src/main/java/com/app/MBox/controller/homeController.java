package com.app.MBox.controller;



import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.dto.songDto;
import com.app.MBox.services.artistService;
import com.app.MBox.services.recordLabelService;
import com.app.MBox.services.songService;
import com.app.MBox.services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/homepage")
    public ModelAndView home(ModelAndView modelAndView, Model model) {
        List<songDto> songs;
        songs=songService.getMostRecentSongs(PageRequest.of(0,5,Sort.Direction.DESC,"dateCreated"));
        List<artistDto>artists;
        artists=artistService.findRecentlyAddedArtist(PageRequest.of(0,5,Sort.Direction.DESC,"dateCreated"));
        model.addAttribute("artists",artists);
        model.addAttribute("songs",songs);
        modelAndView.setViewName("home");
        return modelAndView;

    }

    @RequestMapping(value = "/artists")
    public ModelAndView showArtists(ModelAndView modelAndView,Model model) {
        List<artistDto> artists=artistService.findAllArtists(PageRequest.of(0,25, Sort.Direction.DESC,"date_created"));
        model.addAttribute("artists",artists);
        modelAndView.setViewName("artistsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/record-labels",method = RequestMethod.GET)
    public ModelAndView showRecordLabels(ModelAndView modelAndView,Model model) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(PageRequest.of(0,25, Sort.Direction.DESC,"dateCreated"));
        model.addAttribute("recordLabels",recordLabels);
        modelAndView.setViewName("recordLabelsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/artist-lazyLoad",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processArtistLazyLoad(Pageable pageable) {
        List<artistDto> artists=artistService.findAllArtists(pageable);
        return artists;
    }

    @RequestMapping(value = "/record-label-lazyLoad",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processRecordLabelLazyLoad(Pageable pageable) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(pageable);
        return recordLabels;
    }

    @RequestMapping(value = "/about")
    public ModelAndView showAbout(ModelAndView modelAndView) {
        modelAndView.setViewName("about");
        return modelAndView;
    }



}
