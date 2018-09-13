package com.app.MBox.controller;

import com.app.MBox.aditional.emailAlreadyExistsException;
import com.app.MBox.dto.userDto;
import com.app.MBox.core.model.*;
import com.app.MBox.services.userServiceImpl;
import com.app.MBox.services.verificationTokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@Slf4j
public class registerController {

    @Autowired
    private userServiceImpl userServiceImpl;

    @Autowired
    private verificationTokenServiceImpl verificationTokenServiceImpl;


    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        userDto userDto = new userDto();
        model.addAttribute("user", userDto);
        return "registration";
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid userDto accountDto, BindingResult result, HttpServletRequest request) {
        users registered = new users();

        if (!result.hasErrors()) {
                try {
                    registered = userServiceImpl.registerNewUserAccount(accountDto, request);
                    return new ModelAndView("success", "user", accountDto);
                }
                catch (emailAlreadyExistsException e) {
                    log.error(e.getMessage());
                    return new ModelAndView("registration", "user", accountDto);
                }

        }

        return new ModelAndView("registration", "user", accountDto);

    }


    @RequestMapping(value = "/successRegister")
    public String success () {

        return "successRegister";
    }


    @RequestMapping(value = "/confirm" , method = RequestMethod.GET)
    public String showConfirmationPage(@RequestParam("token") String token) {

        boolean result= verificationTokenServiceImpl.confirmUser(token);
        if(result) {

            return "successfullConfirm";
        }   else {
                return "unSuccessfullConfirm";
        }


    }


}
