package com.app.MBox.controller;

import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.validation.passwordChecker;
import com.app.MBox.config.properties;
import com.app.MBox.services.userService;
import com.app.MBox.services.verificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;



@Controller
public class loginController {

    @Autowired
    private userService userServiceImpl;
    @Autowired
    private verificationTokenService verificationTokenServiceImpl;

    @Autowired
    private properties properties;

    @Autowired
    private passwordChecker passwordChecker;
    @Autowired
    private springChecks springChecks;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public ModelAndView login(ModelAndView modelAndView) {
        String role=springChecks.getLoggedInUserRole();
        if(role!=null) {
            modelAndView.addObject("role",role);
            modelAndView.addObject("errorMsg",properties.getNotAuthorizeMessage());
            modelAndView.setViewName("error");
            return modelAndView;
        }
        modelAndView.setViewName("login");
        return modelAndView;

    }

    @RequestMapping(value = "/error" , method = RequestMethod.GET)
    public ModelAndView error(ModelAndView modelAndView) {
        String role=springChecks.getLoggedInUserRole();
        modelAndView.addObject("role",role);
        modelAndView.setViewName("error");
        return modelAndView;

    }
    @RequestMapping(value = "/forgot-password",method = RequestMethod.GET)
    public ModelAndView showForgotPasswordPage() {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }


    @RequestMapping(value = "/forgot-password",method = RequestMethod.POST)
    public ModelAndView processForgotPassword(ModelAndView modelAndView, @RequestParam("email") String userEmail) {
        if(userServiceImpl.forgotPassword(userEmail)) {
                modelAndView.setViewName("passwordResetMail");
                return modelAndView;
        }   else {
            modelAndView.addObject("errorMsg",properties.getIncorrectEmailMessage());
            modelAndView.setViewName("forgotPassword");
            return modelAndView;
        }
    }


    @RequestMapping(value = "/reset-password",method = RequestMethod.GET)
    public ModelAndView showResetPassword(ModelAndView modelAndView, @RequestParam("token") String token) {

        if (verificationTokenServiceImpl.checkTokenExpired(token)) {
            modelAndView.addObject("expiredToken",properties.getExpiredToken());
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
            modelAndView.addObject("errorConfirmMessage",properties.getPasswordNotMatchMessage());
            modelAndView.setViewName("redirect:reset-password?token=" + token);
            return modelAndView;

        }
            userServiceImpl.savePassword(password,token);
            modelAndView.setViewName("confirmationPasswordReset");
            return modelAndView;
    }


}
