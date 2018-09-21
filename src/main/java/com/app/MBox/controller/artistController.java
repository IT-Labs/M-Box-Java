package com.app.MBox.controller;

import com.app.MBox.common.customHandler.authenticatedUser;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.core.model.artist;
import com.app.MBox.dto.songDto;
import com.app.MBox.services.artistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequestMapping(value = "/artist")
public class artistController {

    @Autowired
    artistService artistService;
    @Autowired
    springChecks springChecks;

    @RequestMapping(value = "/song",method = RequestMethod.GET)
    public ModelAndView showSongPage (ModelAndView modelAndView,Model model) {
        songDto songDto=new songDto();
        //artist artist=springChecks.getLoggedInArtist();
        //modelAndView.addObject("artistName","by: " + artist.getUser().getName());
        modelAndView.addObject("artistName","by: Tyga");
        model.addAttribute("songDto",songDto);
        modelAndView.setViewName("artistNewSong");
        return modelAndView;
    }

    @RequestMapping(value = "/song",method = RequestMethod.POST ,consumes = "multipart/form-data")
    public ModelAndView processSongPage(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,@ModelAttribute("songDto") songDto songDto) {
        modelAndView.setViewName("artistNewSong");
        return modelAndView;
    }


    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public ModelAndView showArtistAccountPage(ModelAndView modelAndView,Model model) {
        modelAndView.setViewName("artistAccount");
        return modelAndView;
    }


}
