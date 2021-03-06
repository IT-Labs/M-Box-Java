package com.app.MBox.controller;

import com.amazonaws.util.StringUtils;
import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.config.properties;
import com.app.MBox.core.model.recordLabel;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.csvParseResultDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.services.artistService;
import com.app.MBox.services.recordLabelService;
import com.app.MBox.services.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping(value = "/record-label")
public class recordLabelController {

    @Autowired
    private userService userServiceImpl;

    @Autowired
    private artistService artistServiceImpl;

    @Autowired
    private springChecks springChecks;

    @Autowired
    private properties properties;
    @Autowired
    private recordLabelService recordLabelService;

    public static int ARTIST_LAZY_LOAD_INITIAL_PAGE=0;
    public static int ARTIST_LAZY_LOAD_INITIAL_SIZE=20;


    @RequestMapping(value = "/dashboard" , method = RequestMethod.GET)
    public ModelAndView showAdminDashboard(Model model) {
        ModelAndView modelAndView=new ModelAndView();
        List<artistDto> artists=userServiceImpl.findArtists(springChecks.getLoggedInUserId(),PageRequest.of(ARTIST_LAZY_LOAD_INITIAL_PAGE,ARTIST_LAZY_LOAD_INITIAL_SIZE));
        model.addAttribute("artists",artists);
        modelAndView.setViewName("recordLabelDashboard");
        return modelAndView;
    }

    @RequestMapping(value = "/pageable-artists",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processSort(Pageable pageable) {
        List<artistDto> artists=userServiceImpl.findArtists(pageable);
        return artists;
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processSearch(@RequestParam String searchParam) {
        List<artistDto> artists=new LinkedList<>();
        if(!StringUtils.isNullOrEmpty(searchParam)) {
            artists = userServiceImpl.searchArtists(searchParam);
            return artists;
        }    else {
            return artists;
        }

    }


    @RequestMapping(value = "/invite",method = RequestMethod.GET)
    public ModelAndView showInviteArtistPage(ModelAndView modelAndView) {
        modelAndView.setViewName("inviteArtist");
        return modelAndView;
    }

    @RequestMapping(value = "/invite",method = RequestMethod.POST)
    public ModelAndView processArtistInvite(ModelAndView modelAndView, @RequestParam("name") String name, @RequestParam("email") String email) {
        try {
            users user=artistServiceImpl.inviteArtist(name,email);
            if(user==null) {
                modelAndView.addObject("artistNumberError", properties.getArtistNumberMessage());
                modelAndView.setViewName("inviteArtist");
                return modelAndView;
            }
            modelAndView.addObject("inviteArtistMessage","You have successfully invited " + name);
            modelAndView.setViewName("confirmationInviteArtist");
            return modelAndView;
        } catch (emailAlreadyExistsException e) {
            log.error(e.getMessage());
            modelAndView.addObject("emailAlreadyExistsError",properties.getEmailAlreadyExistsMessage());
            modelAndView.setViewName("inviteArtist");
            return modelAndView;
        }

    }

    @RequestMapping(value = "/artists",method = RequestMethod.GET)
    public ModelAndView showAddMultipleArtistPage(ModelAndView modelAndView) {
        modelAndView.setViewName("addMultipleArtists");
        return modelAndView;
    }

    @PostMapping(value = "/artists" , consumes = "multipart/form-data")
    public ModelAndView addArtists(ModelAndView modelAndView, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if(file.isEmpty()) {
            modelAndView.addObject("emptyFile",properties.getCsvExtensionError());
            modelAndView.setViewName("addMultipleArtists");
            return modelAndView;
        }
        String [] extension=file.getOriginalFilename().split("\\.");
        if(!extension[extension.length-1].equals("csv")) {
            modelAndView.addObject("invalidExtension",properties.getCsvExtensionError());
            modelAndView.setViewName("addMultipleArtists");
            return modelAndView;
        }

        try {
            csvParseResultDto result=artistServiceImpl.addArtistsByCsvFile(file);
            if(result.isHasErrors()) {
                modelAndView.addObject("artistLimitExceded",result.getArtistLimitExceded());
                modelAndView.addObject("invalidEmailFormat",result.getInvalidEmailFormatDetectedRows());
                modelAndView.addObject("invalidFormatDetected",result.getInvalidFormatDetectedRows());
                modelAndView.addObject("maxLengthOfArtistName",result.getMaxLengthOfArtistNameRows());
                modelAndView.addObject("maxLengthOfArtistEmail",result.getMaxLengthOfEmailRows());
                modelAndView.setViewName("addMultipleArtists");
                return modelAndView;
            }
            modelAndView.addObject("artistAdded" ,String.format("%d new artists successfully added. Current artist status %d/50",result.getArtistAdded(),result.getArtistAdded()+result.getNumber()));
            modelAndView.setViewName("confirmationAddMultipleArtists");
            return modelAndView;
        }   catch (Exception e ) {
            log.error(e.getMessage());
        }

        modelAndView.setViewName("addMultipleArtists");
        return modelAndView;
    }

    @RequestMapping(value = "/artists/confirmation",method = RequestMethod.GET)
    public ModelAndView showConfirmationAddMultipleArtists(ModelAndView modelAndView) {
        modelAndView.setViewName("confirmationAddMultipleArtists");
        return modelAndView;
    }

    @RequestMapping(value = "/artist/{email}",method = RequestMethod.DELETE)
    public ModelAndView processDeleteArtist(ModelAndView modelAndView,@PathVariable("email") String email) {
        artistServiceImpl.deleteArtist(email);
        modelAndView.setViewName("recordLabelDashboard");
        return modelAndView;
    }

    @RequestMapping(value="/account",method = RequestMethod.GET)
    public ModelAndView showRecordLabelAccountPage(ModelAndView modelAndView,Model model) {
        recordLabel recordLabel=springChecks.getLoggedInRecordLabel();
        recordLabelDto recordLabelDto=recordLabelService.findRecordLabel(recordLabel.getId());
        model.addAttribute("record",recordLabelDto);
        modelAndView.setViewName("recordLabelAccount");
        return modelAndView;
    }

    @RequestMapping(value = "/account",method = RequestMethod.POST)
    public ModelAndView processRecordLabelAccountPage(ModelAndView modelAndView,@ModelAttribute("recordLabelDto") recordLabelDto recordLabelDto) {
        recordLabelService.saveRecordLabel(recordLabelDto);
        return modelAndView;
    }

    @RequestMapping(value = "/picture",method = RequestMethod.POST)
    public ModelAndView processArtistPicture(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,@RequestParam("id") int id) {
        String result=recordLabelService.addPicture(file,id);
        if(result.equals("wrongFormat")) {
            modelAndView.addObject(result,properties.getImageExtensionError());
        } else if (result.equals("sizeExceeded")) {
            modelAndView.addObject(result,properties.getMaxUploadImageSize());
        }

        modelAndView.setViewName("redirect:account");
        return modelAndView;
    }


}
