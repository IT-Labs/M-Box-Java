package com.app.MBox.controller;

import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.common.validation.passwordChecker;
import com.app.MBox.common.properties;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.changePasswordDto;
import com.app.MBox.services.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;



@Controller
@Slf4j
public class userController {
    @Autowired
    userService userServiceImpl;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    properties properties;
    @Autowired
    passwordChecker passwordChecker;
    @Autowired
    verificationTokenService verificationTokenServiceImpl;
    @Autowired
    springChecks springChecks;
    @Autowired
    userRolesService userRolesService;


    @RequestMapping(value = "/change-password",method = RequestMethod.GET)
    public ModelAndView showChangePassword(ModelAndView model) {
        changePasswordDto changePasswordDto=new changePasswordDto();
        model.addObject("changePasswordDto",changePasswordDto);
        model.setViewName("changePassword");
        return model;
    }

    @RequestMapping(value = "/change-password",method = RequestMethod.POST)
    public ModelAndView processChangePassword(@ModelAttribute("changePasswordDto") changePasswordDto changePasswordDto) {
        users user=userServiceImpl.findByEmail(springChecks.getLoggedInUserEmail());
        ModelAndView modelAndView=new ModelAndView();
        if(!bCryptPasswordEncoder.matches(changePasswordDto.getPassword(),user.getPassword())) {
            modelAndView.addObject("oldPasswordErrorMessage",properties.getIncorectPasswordMessage());
            modelAndView.setViewName("changePassword");
            return modelAndView;
        }   else if (passwordChecker.isInvalidPassword(changePasswordDto.getNewPassword())) {
            modelAndView.addObject("errorMessage",properties.getPasswordMessage());
            modelAndView.setViewName("changePassword");
            return modelAndView;
        }   else if (!passwordChecker.doPasswordMatches(changePasswordDto.getNewPassword(),changePasswordDto.getConfirmPassword())) {
            modelAndView.addObject("errorConfirmMessage",properties.getPasswordNotMatchMessage());
            modelAndView.setViewName("changePassword");
            return modelAndView;
        }
        userServiceImpl.setUserPassword(user,changePasswordDto.getNewPassword());
        String role=springChecks.getLoggedInUserRole();
        if(role.equals(rolesEnum.ARTIST)) {
            modelAndView.addObject("role",rolesEnum.ARTIST.toString());
        } else if (role.equals(rolesEnum.RECORDLABEL)) {
            modelAndView.addObject("role",rolesEnum.RECORDLABEL.toString());
        }   else {
            modelAndView.addObject("role",rolesEnum.LISTENER.toString());
        }
        modelAndView.setViewName("confirmationChangePassword");
        return modelAndView;

    }


    @RequestMapping(value = "/join" , method = RequestMethod.GET)
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

    @RequestMapping(value = "/join",method = RequestMethod.POST)
    public ModelAndView processResetPassword(ModelAndView modelAndView, @RequestParam("token") String token,@RequestParam("password") String password,@RequestParam("ConfirmPassword") String confirmPassword) {

        if(passwordChecker.isInvalidPassword(password)) {
            modelAndView.addObject("errorMessage",properties.getPasswordMessage());
            modelAndView.setViewName("redirect:join?token=" + token);
            return modelAndView;
        }   else if (!passwordChecker.doPasswordMatches(password,confirmPassword)) {
            modelAndView.addObject("errorConfirmMessage",properties.getPasswordNotMatchMessage());
            modelAndView.setViewName("redirect:join?token=" + token);
            return modelAndView;

        }
        if(userRolesService.getUserRoleByToken(token).equals(rolesEnum.ARTIST.toString())) {
            modelAndView.setViewName("artistAccount");
        }   else {
            modelAndView.setViewName("recordLabelDashboard");
        }
        userServiceImpl.setUserPassword(token,password);


        return modelAndView;
    }


}
