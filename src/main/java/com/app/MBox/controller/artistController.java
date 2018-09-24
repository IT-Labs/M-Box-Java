package com.app.MBox.controller;

import com.app.MBox.common.customHandler.authenticatedUser;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.core.model.artist;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.songDto;
import com.app.MBox.services.artistService;
import com.app.MBox.services.songService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/artist")
public class artistController {

    @Autowired
    artistService artistService;
    @Autowired
    springChecks springChecks;
    @Autowired
    songService songService;

    @RequestMapping(value = "/song",method = RequestMethod.GET)
    public ModelAndView showSongPage (ModelAndView modelAndView,Model model) {
        songDto songDto=new songDto();
        artist artist=springChecks.getLoggedInArtist();
        modelAndView.addObject("artistName","by: " + artist.getUser().getName());
        model.addAttribute("songDto",songDto);
        modelAndView.setViewName("artistNewSong");
        return modelAndView;
    }

    @RequestMapping(value = "/song",method = RequestMethod.POST)
    public ModelAndView processSongPage(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,@ModelAttribute("songDto") songDto songDto) {
        String result=songService.isValidPicture(file);
        if(result.equals("wrongFormat")) {
            modelAndView.addObject(result,"You can only upload .jpg, .jpeg or .png!");
            modelAndView.setViewName("artistNewSong");
            return modelAndView;
        } else if (result.equals("sizeExceeded")) {
            modelAndView.addObject(result,"Upload file exceeded the maximum file size limit of 3MB!");
            modelAndView.setViewName("artistNewSong");
            return modelAndView;
        }

        songService.addSong(file,songDto);
        modelAndView.setViewName("artistAccount");
        return modelAndView;
    }


    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public ModelAndView showArtistAccountPage(ModelAndView modelAndView,Model model) {
        modelAndView.setViewName("artistAccount");
        return modelAndView;
    }

    @RequestMapping(value = "/songs",method = RequestMethod.GET)
    public ModelAndView showArtistMySongsPage(ModelAndView modelAndView,Model model) {
        List<songDto> songs=songService.findSongs(PageRequest.of(0,20));
        model.addAttribute("songs",songs);
        artist artist=springChecks.getLoggedInArtist();
        artistDto artistDto=new artistDto();
        artistDto.setDeleted(artist.isDeleted());
        model.addAttribute("artist",artistDto);
        modelAndView.setViewName("artistMySongs");
        return modelAndView;
    }

    @RequestMapping(value = "/delete-song",method = RequestMethod.GET)
    public ModelAndView deleteSong(ModelAndView modelAndView,@RequestParam ("id") int id) {
        songService.deleteSong(id);
        modelAndView.setViewName("redirect:songs");
        return modelAndView;
    }

    @RequestMapping(value = "/lazyLoad",method = RequestMethod.GET)
    @ResponseBody
    public List<songDto> processLazyLoading(Pageable pageable) {
        List<songDto> songDtos=new LinkedList<>();
        songDtos=songService.findSongs(pageable);
        return songDtos;
    }

    @RequestMapping(value = "/sort",method = RequestMethod.GET)
    @ResponseBody
    public List<songDto> processSongsSort(Pageable pageable) {
        List<songDto> songs=new LinkedList<>();
        songs=songService.findSongs(pageable);
        return songs;
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public List<songDto> processSearch(@RequestParam String searchParam) {
        List<songDto> songs=new LinkedList<>();
        if(!searchParam.equals("")) {
            songs = songService.searchSongs(searchParam);
            return songs;
        }    else {
            return songs;
        }

    }
}
