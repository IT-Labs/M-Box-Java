package com.app.MBox.controller;

import com.amazonaws.util.StringUtils;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.config.properties;
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
    springChecks springChecks;
    @Autowired
    songService songService;
    @Autowired
    properties properties;
    @Autowired
    artistService artistService;

    public static int INITIAL_PAGE=0;
    public static int INITIAL_SIZE=20;

    @RequestMapping(value = "/song",method = RequestMethod.GET)
    public ModelAndView showSongPage (ModelAndView modelAndView,Model model) {
        songDto songDto=new songDto();
        artist artist=springChecks.getLoggedInArtist();
        modelAndView.addObject("artistName",String.format("by: %s",artist.getUser().getName()));
        model.addAttribute("songDto",songDto);
        modelAndView.setViewName("artistNewSong");
        return modelAndView;
    }

    @RequestMapping(value = "/song",method = RequestMethod.POST)
    public ModelAndView processSongPage(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,@ModelAttribute("songDto") songDto songDto) {
        String result=songService.addSong(file,songDto);
        if(result.equals("wrongFormat")) {
            modelAndView.addObject(result,properties.getImageExtensionError());
            modelAndView.setViewName("artistNewSong");
            return modelAndView;
        } else if (result.equals("sizeExceeded")) {
            modelAndView.addObject(result,properties.getMaxUploadImageSize());
            modelAndView.setViewName("artistNewSong");
            return modelAndView;
        }

        modelAndView.setViewName("confirmationPublishSong");
        return modelAndView;
    }


    @RequestMapping(value = "/songs",method = RequestMethod.GET)
    public ModelAndView showArtistMySongsPage(ModelAndView modelAndView,Model model) {
        List<songDto> songs=songService.findSongs(PageRequest.of(INITIAL_PAGE,INITIAL_SIZE));
        model.addAttribute("songs",songs);
        artist artist=springChecks.getLoggedInArtist();
        artistDto artistDto=new artistDto();
        artistDto.setDeleted(artist.isDeleted());
        model.addAttribute("artist",artistDto);
        modelAndView.setViewName("artistMySongs");
        return modelAndView;
    }

    @RequestMapping(value = "/song/{id}",method = RequestMethod.DELETE)
    public ModelAndView deleteSong(ModelAndView modelAndView,@PathVariable ("id") int id) {
        songService.deleteSong(id);
        modelAndView.setViewName("artistAccount");
        return modelAndView;
    }

     //Used for lazy loading and sorting
    @RequestMapping(value = "/pageable-songs",method = RequestMethod.GET)
    @ResponseBody
    public List<songDto> processPageableSongs(Pageable pageable) {
        List<songDto> songDtos=new LinkedList<>();
        songDtos=songService.findSongs(pageable);
        return songDtos;
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public List<songDto> processSearch(@RequestParam String searchParam) {
        List<songDto> songs=new LinkedList<>();
        if(!StringUtils.isNullOrEmpty(searchParam) ) {
            songs = songService.searchSongs(searchParam);
            }
            return songs;

    }

    @RequestMapping(value = "/edit-song",method = RequestMethod.POST)
    public ModelAndView showArtistAccountPage(@ModelAttribute("songDto") songDto song) {
        songService.saveSong(song);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("artistAccount");
        return modelAndView;
    }

    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public ModelAndView showMyAccountPage(ModelAndView modelAndView,Model model) {
        artist artist=springChecks.getLoggedInArtist();
        List<artist> artists=new LinkedList<>();
        artists.add(artist);
        List<artistDto> artistDtos=artistService.mapArtistToArtistDto(artists);
        model.addAttribute("artist",artistDtos.get(0));
        modelAndView.setViewName("artistAccount");
        return modelAndView;
    }

    @RequestMapping(value = "/account",method = RequestMethod.POST)
    public ModelAndView editArtistAccountPage(ModelAndView modelAndView,@ModelAttribute("artistDto") artistDto artist) {
        artistService.saveArtist(artist);
        return modelAndView;
    }

    @RequestMapping(value = "/picture",method = RequestMethod.POST)
    public ModelAndView processArtistPicture(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,@RequestParam("id") int id) {
        String result=artistService.addPicture(file,id);
        if(result.equals("wrongFormat")) {
            modelAndView.addObject(result,properties.getImageExtensionError());
            modelAndView.setViewName("redirect:account");
            return modelAndView;
        } else if (result.equals("sizeExceeded")) {
            modelAndView.addObject(result,properties.getMaxUploadImageSize());
            modelAndView.setViewName("redirect:account");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:account");
        return modelAndView;
    }

    @RequestMapping(value = "/song-picture",method = RequestMethod.POST)
    public ModelAndView processSongPicture(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,@RequestParam("id") int id) {
        String result=songService.addPicture(file,id);
        if(result.equals("wrongFormat")) {
            modelAndView.addObject(result,properties.getImageExtensionError());
            modelAndView.setViewName(String.format("redirect:/home/song?id=%d",id));
            return modelAndView;
        } else if (result.equals("sizeExceeded")) {
            modelAndView.addObject(result,properties.getMaxUploadImageSize());
            modelAndView.setViewName(String.format("redirect:/home/song?id=%d",id));
            return modelAndView;
        }

        modelAndView.setViewName(String.format("redirect:/home/song?id=%d",id));
        return modelAndView;
    }
}
