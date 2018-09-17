package com.app.MBox.controller;


import com.app.MBox.aditional.authenticatedUser;
import com.app.MBox.aditional.emailAlreadyExistsException;
import com.app.MBox.aditional.passwordChecker;
import com.app.MBox.aditional.properties;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.changePasswordDto;
import com.app.MBox.dto.csvErrorsDto;
import com.app.MBox.dto.userDto;
import com.app.MBox.services.artistServiceImpl;
import com.app.MBox.services.recordLabelServiceImpl;
import com.app.MBox.services.userServiceImpl;
import com.app.MBox.services.verificationTokenServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


@Controller
@Slf4j
public class userController {
    @Autowired
    userServiceImpl userServiceImpl;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    properties properties;
    @Autowired
    passwordChecker passwordChecker;
    @Autowired
    recordLabelServiceImpl recordLabelServiceImpl;
    @Autowired
    verificationTokenServiceImpl verificationTokenServiceImpl;
    @Autowired
    artistServiceImpl artistServiceImpl;

    @RequestMapping(value = "/changePassword",method = RequestMethod.GET)
    public Model showChangePassword(Model model) {
        changePasswordDto changePasswordDto=new changePasswordDto();
        model.addAttribute("changePasswordDto",changePasswordDto);
        return model;
    }

    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public ModelAndView processChangePassword(@ModelAttribute("changePasswordDto") changePasswordDto changePasswordDto) {

        String name=SecurityContextHolder.getContext().getAuthentication().getName();
        users user=userServiceImpl.findByEmail(name);
        ModelAndView modelAndView=new ModelAndView();
        if(!bCryptPasswordEncoder.matches(changePasswordDto.getPassword(),user.getPassword())) {
            modelAndView.addObject("oldPasswordErrorMessage","Incorrect password");
            modelAndView.setViewName("changePassword");
            return modelAndView;
        }   else if (passwordChecker.isInvalidPassword(changePasswordDto.getNewPassword())) {
            modelAndView.addObject("errorMessage",properties.getPasswordMessage());
            modelAndView.setViewName("changePassword");
            return modelAndView;
        }   else if (!passwordChecker.doPasswordMatches(changePasswordDto.getNewPassword(),changePasswordDto.getConfirmPassword())) {
            modelAndView.addObject("errorConfirmMessage","Password does not match");
            modelAndView.setViewName("changePassword");
            return modelAndView;
        }
        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getPassword()));
        userServiceImpl.saveUser(user);
        modelAndView.setViewName("ConfirmationChangePassword");
        return modelAndView;

    }


    @RequestMapping(value = "/joinIfInvited" , method = RequestMethod.GET)
    public ModelAndView showJoinIfInvitedPage(ModelAndView modelAndView, @RequestParam("token") String token) {
        if (verificationTokenServiceImpl.checkTokenExpired(token)) {
            modelAndView.setViewName("error");
            return modelAndView;
        }   else {
            modelAndView.addObject("confirmationToken",token);
            modelAndView.setViewName("joinIfInvited");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/joinIfInvited",method = RequestMethod.POST)
    public ModelAndView processResetPassword(ModelAndView modelAndView, @RequestParam("token") String token,@RequestParam("password") String password,@RequestParam("ConfirmPassword") String confirmPassword) {

        if(passwordChecker.isInvalidPassword(password)) {
            modelAndView.addObject("errorMessage",properties.getPasswordMessage());
            modelAndView.setViewName("redirect:joinIfInvited?token=" + token);
            return modelAndView;
        }   else if (!passwordChecker.doPasswordMatches(password,confirmPassword)) {
            modelAndView.addObject("errorConfirmMessage","Passwords does not match");
            modelAndView.setViewName("redirect:joinIfInvited?token=" + token);
            return modelAndView;

        }
        userServiceImpl.setUserPassword(token,password);
        //see in brd maybe you should redirect them on diferent pages
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @RequestMapping(value = "/recordLabelDashboard" , method = RequestMethod.GET)
    public ModelAndView showAdminDashboard(Model model) {
        ModelAndView modelAndView=new ModelAndView();
        authenticatedUser authenticatedUser=(authenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<artistDto> artists=userServiceImpl.findArtists(authenticatedUser.getUserId(),0);
        model.addAttribute("artists",artists);
        modelAndView.setViewName("recordLabelDashboard");
        return modelAndView;
    }

    @RequestMapping(value = "/sort",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processSort(@RequestParam String sortParam,@RequestParam int page,@RequestParam int direction) {
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

    @RequestMapping(value = "/inviteArtist",method = RequestMethod.GET)
    public ModelAndView showInviteArtistPage(ModelAndView modelAndView) {
        modelAndView.setViewName("inviteArtist");
        return modelAndView;
    }

    @RequestMapping(value = "/inviteArtist",method = RequestMethod.POST)
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

    @RequestMapping(value = "/addMultipleArtists",method = RequestMethod.GET)
    public ModelAndView showAddMultipleArtistPage(ModelAndView modelAndView) {
        modelAndView.setViewName("addMultipleArtists");
        return modelAndView;
    }
    @PostMapping(value = "/addMultipleArtists" , consumes = "multipart/form-data")
    public ModelAndView processCsv(ModelAndView modelAndView,@RequestParam("file") MultipartFile file,HttpServletRequest request) {
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
              csvErrorsDto errors=artistServiceImpl.parseCsv(file,request);
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

    @RequestMapping(value = "/confirmationAddMultipleArtists",method = RequestMethod.GET)
    public ModelAndView showConfirmationAddMultipleArtists(ModelAndView modelAndView) {
        modelAndView.setViewName("confirmationAddMultipleArtists");
        return modelAndView;
    }

}
