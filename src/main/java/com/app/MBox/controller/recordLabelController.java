package com.app.MBox.controller;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.customHandler.authenticatedUser;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.csvParseResultDto;
import com.app.MBox.services.artistService;
import com.app.MBox.services.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/recordLabel")
public class recordLabelController {

    @Autowired
    userService userServiceImpl;

    @Autowired
    artistService artistServiceImpl;

    public static int RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE=0;


    @RequestMapping(value = "/dashboard" , method = RequestMethod.GET)
    public ModelAndView showAdminDashboard(Model model) {
        ModelAndView modelAndView=new ModelAndView();
        authenticatedUser authenticatedUser=(authenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<artistDto> artists=userServiceImpl.findArtists(authenticatedUser.getUserId(),RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE);
        model.addAttribute("artists",artists);
        modelAndView.setViewName("recordLabelDashboard");
        return modelAndView;
    }

    @RequestMapping(value = "/sort",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processSort(@RequestParam String sortParam, @RequestParam int page, @RequestParam int direction) {
        List<artistDto> artists=new LinkedList<>();
        artists=userServiceImpl.findAndSortArtists(sortParam,page,direction);
        return artists;
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processSearch(@RequestParam String searchParam) {
        List<artistDto> artists=new LinkedList<>();
        if(!searchParam.equals("")) {
            artists = userServiceImpl.searchArtists(searchParam);
            return artists;
        }    else {
            return null;
        }

    }


    @RequestMapping(value = "/lazyLoad",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processLazyLoading(@RequestParam int page) {
        List<artistDto> artistDtos=new LinkedList<>();
        authenticatedUser authenticatedUser=(authenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        artistDtos=userServiceImpl.findArtists(authenticatedUser.getUserId(),page);
        return artistDtos;
    }

    @RequestMapping(value = "/invite",method = RequestMethod.GET)
    public ModelAndView showInviteArtistPage(ModelAndView modelAndView) {
        modelAndView.setViewName("inviteArtist");
        return modelAndView;
    }

    @RequestMapping(value = "/invite",method = RequestMethod.POST)
    public ModelAndView processArtistInvite(ModelAndView modelAndView, @RequestParam("name") String name, @RequestParam("email") String email, HttpServletRequest request) {
        try {
            users user=artistServiceImpl.inviteArtist(name,email,request);
            if(user==null) {
                modelAndView.addObject("artistNumberError","Artist limit (50) exceeded");
                modelAndView.setViewName("inviteArtist");
                return modelAndView;
            }
            modelAndView.addObject("inviteArtistMessage","You have successfully invited " + name);
            modelAndView.setViewName("confirmationInviteArtist");
            return modelAndView;
        } catch (emailAlreadyExistsException e) {
            log.error(e.getMessage());
            modelAndView.addObject("emailAlreadyExistsError","email already exists");
            modelAndView.setViewName("inviteArtist");
            return modelAndView;
        }

    }

    @RequestMapping(value = "/add-artists",method = RequestMethod.GET)
    public ModelAndView showAddMultipleArtistPage(ModelAndView modelAndView) {
        modelAndView.setViewName("addMultipleArtists");
        return modelAndView;
    }
    @PostMapping(value = "/add-artists" , consumes = "multipart/form-data")
    public ModelAndView processCsv(ModelAndView modelAndView, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if(file.isEmpty()) {
            modelAndView.addObject("emptyFile","Please select a .csv file");
            modelAndView.setViewName("addMultipleArtists");
            return modelAndView;
        }
        String [] extension=file.getOriginalFilename().split("\\.");
        if(!extension[extension.length-1].equals("csv")) {
            modelAndView.addObject("invalidExtension","Uploaded file must be .csv");
            modelAndView.setViewName("addMultipleArtists");
            return modelAndView;
        }

        try {
            csvParseResultDto errors=artistServiceImpl.parseCsv(file,request);
            if(errors.isErrorFlag()) {
                modelAndView.addObject("artistLimitExceded",errors.getArtistLimitExceded());
                modelAndView.addObject("invalidEmailFormat",errors.getInvalidEmailFormatDetectedRows());
                modelAndView.addObject("invalidFormatDetected",errors.getInvalidFormatDetectedRows());
                modelAndView.addObject("maxLengthOfArtistName",errors.getMaxLengthOfArtistNameRows());
                modelAndView.addObject("maxLengthOfArtistEmail",errors.getMaxLengthOfEmailRows());
                modelAndView.setViewName("addMultipleArtists");
                return modelAndView;
            }
            modelAndView.addObject("artistAdded" ,String.format("%d new artists successfully added. Current artist status %d/50",errors.getArtistAdded(),errors.getArtistAdded()+errors.getNumber()));
            modelAndView.setViewName("confirmationAddMultipleArtists");
            return modelAndView;


        }   catch (Exception e ) {
            log.error(e.getMessage());
        }

        modelAndView.setViewName("addMultipleArtists");
        return modelAndView;
    }

    @RequestMapping(value = "/confirmation-artists",method = RequestMethod.GET)
    public ModelAndView showConfirmationAddMultipleArtists(ModelAndView modelAndView) {
        modelAndView.setViewName("confirmationAddMultipleArtists");
        return modelAndView;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public ModelAndView processDeleteArtist(ModelAndView modelAndView,@RequestParam("email") String email) {
        artistServiceImpl.deleteArtist(email);
        modelAndView.setViewName("redirect:dashboard");
        return modelAndView;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public ModelAndView processDeleteArtistGet(ModelAndView modelAndView,@RequestParam("email") String email) {
        artistServiceImpl.deleteArtist(email);
        modelAndView.setViewName("redirect:dashboard");
        return modelAndView;
    }


}
