package com.app.MBox.controller;



import com.app.MBox.config.properties;
import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.song;
import com.app.MBox.dto.aboutMessageDto;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.dto.songDto;
import com.app.MBox.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping(value = "/home")
public class homeController {
    @Autowired
    songService songService;
    @Autowired
    artistService artistService;
    @Autowired
    recordLabelService recordLabelService;
    @Autowired
    emailService emailService;
    @Autowired
    properties properties;

    public static int INITIAL_PAGE=0;
    public static int INITIAL_SIZE=25;
    public static int INITIAL_HOMEPAGE_SIZE=5;
    public static String INITIAL_SORT_PARAMETAR="dateCreated";
    public static Sort.Direction INITIAL_SORT_DIRECTION=Sort.Direction.DESC;

    @GetMapping("/homepage")
    public ModelAndView home(ModelAndView modelAndView, Model model) {
        List<songDto> songs;
        songs=songService.getMostRecentSongs(PageRequest.of(INITIAL_PAGE,INITIAL_HOMEPAGE_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        List<artistDto>artists;
        artists=artistService.findRecentlyAddedArtist(PageRequest.of(INITIAL_PAGE,INITIAL_HOMEPAGE_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        model.addAttribute("artists",artists);
        model.addAttribute("songs",songs);
        modelAndView.setViewName("home");
        return modelAndView;

    }

    @RequestMapping(value = "/artist")
    public ModelAndView showArtists(ModelAndView modelAndView,Model model) {
        List<artistDto> artists=artistService.findAllArtists(PageRequest.of(INITIAL_PAGE,INITIAL_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        model.addAttribute("artists",artists);
        modelAndView.setViewName("artistsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/record-label",method = RequestMethod.GET)
    public ModelAndView showRecordLabels(ModelAndView modelAndView,Model model) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(PageRequest.of(INITIAL_PAGE,INITIAL_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        model.addAttribute("recordLabels",recordLabels);
        modelAndView.setViewName("recordLabelsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/lazy-load-artist",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processArtistLazyLoad(Pageable pageable) {
        List<artistDto> artists=artistService.findAllArtists(pageable);
        return artists;
    }

    @RequestMapping(value = "/lazy-load-record-label",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processRecordLabelLazyLoad(Pageable pageable) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(pageable);
        return recordLabels;
    }

    @RequestMapping(value = "/about",method = RequestMethod.GET)
    public ModelAndView showAbout(ModelAndView modelAndView,Model model) {
        List<recordLabelDto> recordLabels=recordLabelService.getAllRecordLabels();
        aboutMessageDto aboutMessageDto=new aboutMessageDto();
        model.addAttribute("recordLabels",recordLabels);
        model.addAttribute("aboutMessageDto",aboutMessageDto);
        modelAndView.setViewName("about");
        return modelAndView;
    }

    @RequestMapping(value = "/about",method = RequestMethod.POST)
    public ModelAndView proccesAbout(ModelAndView modelAndView,@ModelAttribute("aboutMessageDto") @Valid aboutMessageDto aboutMessageDto) {
        emailService.sendAboutEmail(aboutMessageDto);
        modelAndView.addObject("successfullMessage",properties.getSuccessfullMessage());
        modelAndView.setViewName("about");
        return modelAndView;
    }

    @RequestMapping(value = "/song",method = RequestMethod.GET)
    public ModelAndView showSongDetails(ModelAndView modelAndView,Model model,@RequestParam("id") int id) {
        song song=songService.findById(id);
        List<song> songs=new LinkedList<>();
        songs.add(song);
        List<songDto> songDtos=songService.mapSongToSongDto(songs);
        model.addAttribute("song",songDtos.get(0));
        modelAndView.setViewName("songDetails");
        return modelAndView;
    }

    @RequestMapping(value = "/artist-details",method = RequestMethod.GET)
    public ModelAndView showArtistDetails(ModelAndView modelAndView,Model model,@RequestParam("id") int id) {
        artist artist=artistService.findById(id);
        List<artist> artists=new LinkedList<>();
        artists.add(artist);
        List<artistDto> artistDtos=artistService.mapArtistToArtistDto(artists);
        model.addAttribute("artist",artistDtos.get(0));
        List<song> songs=songService.findByArtistId(id);
        List<songDto> songDtos=songService.mapSongToSongDto(songs);
        model.addAttribute("songs",songDtos);
        modelAndView.setViewName("artistDetails");
        return modelAndView;
    }



}
