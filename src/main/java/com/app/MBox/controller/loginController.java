package com.app.MBox.controller;

import com.app.MBox.common.validation.passwordChecker;
import com.app.MBox.common.properties;
import com.app.MBox.services.userService;
import com.app.MBox.services.userServiceImpl;
import com.app.MBox.services.verificationTokenService;
import com.app.MBox.services.verificationTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;



@Controller
public class loginController {

    @Autowired
    userService userServiceImpl;
    @Autowired
    verificationTokenService verificationTokenServiceImpl;

    @Autowired
    properties properties;

    @Autowired
    passwordChecker passwordChecker;

    @GetMapping(value = "/login")
    public ModelAndView login() {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;

    }

    @GetMapping("/error")
    public ModelAndView error() {
        ModelAndView modelAndView=new ModelAndView();
        String errorMessage="You are not authorize for the requested data";
        modelAndView.addObject("errorMsg",errorMessage);
        modelAndView.setViewName("error");

        return modelAndView;

    }
    @GetMapping("/forgot-password")
    public ModelAndView showForgotPasswordPage() {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }


    @RequestMapping(value = "/forgot-password",method = RequestMethod.POST)
    public ModelAndView processForgotPassword(ModelAndView modelAndView, @RequestParam("email") String userEmail, HttpServletRequest request) {


        if(userServiceImpl.forgotPassword(userEmail,request)) {
                modelAndView.setViewName("passwordResetMail");
                return modelAndView;

        }   else {
            modelAndView.addObject("errorMsg","Incorrect email");
            modelAndView.setViewName("forgotPassword");
            return modelAndView;
        }


    }


    @RequestMapping(value = "/reset-password",method = RequestMethod.GET)
    public ModelAndView showResetPassword(ModelAndView modelAndView, @RequestParam("token") String token) {

        if (verificationTokenServiceImpl.checkTokenExpired(token)) {
            modelAndView.setViewName("error");
            return modelAndView;
        }   else {
            modelAndView.addObject("confirmationToken",token);
            modelAndView.setViewName("resetPassword");
            return modelAndView;
        }


    }

    @RequestMapping(value = "/reset-password",method = RequestMethod.POST)
    public ModelAndView processResetPassword(ModelAndView modelAndView, @RequestParam("token") String token,@RequestParam("password") String password,@RequestParam("ConfirmPassword") String confirmPassword) {

        if(passwordChecker.isInvalidPassword(password)) {
            modelAndView.addObject("errorMessage",properties.getPasswordMessage());
            modelAndView.setViewName("redirect:reset-password?token=" + token);
            return modelAndView;
        }   else if (!passwordChecker.doPasswordMatches(password,confirmPassword)) {
            modelAndView.addObject("errorConfirmMessage","Passwords does not match");
            modelAndView.setViewName("redirect:reset-password?token=" + token);
            return modelAndView;

        }
            userServiceImpl.savePassword(password,token);
            modelAndView.setViewName("confirmationPasswordReset");
            return modelAndView;
    }


}
