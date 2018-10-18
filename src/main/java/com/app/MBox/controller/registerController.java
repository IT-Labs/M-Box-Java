package com.app.MBox.controller;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.config.properties;
import com.app.MBox.dto.userDto;
import com.app.MBox.services.userService;
import com.app.MBox.services.verificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;

@Controller
@Slf4j
public class registerController {

    @Autowired
    private userService userServiceImpl;

    @Autowired
    private verificationTokenService verificationTokenServiceImpl;

    @Autowired
    private properties properties;


    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm(ModelAndView modelAndView, Model model) {
        userDto userDto = new userDto();
        model.addAttribute("user", userDto);
        modelAndView.setViewName("registration");
        return modelAndView;
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid userDto accountDto, BindingResult result) {
        if (!result.hasErrors()) {
                try {
                    userServiceImpl.registerNewUserAccount(accountDto);
                    return new ModelAndView("successRegister");
                }
                catch (emailAlreadyExistsException e) {
                    log.error(e.getMessage());
                    ModelAndView modelAndView=new ModelAndView();
                    modelAndView.addObject("userAlreadyExists",properties.getEmailAlreadyExistsMessage());
                    modelAndView.addObject("user",accountDto);
                    modelAndView.setViewName("registration");
                    return modelAndView;
                }

        }

        return new ModelAndView("registration", "user", accountDto);

    }


    @RequestMapping(value = "/registration/success",method = RequestMethod.GET)
    public ModelAndView success (ModelAndView modelAndView) {
        modelAndView.setViewName("successRegister");
        return modelAndView;
    }


    @RequestMapping(value = "/confirm" , method = RequestMethod.GET)
    public ModelAndView showConfirmationPage(ModelAndView modelAndView,@RequestParam("token") String token) {

        boolean result= verificationTokenServiceImpl.confirmUser(token);
        if(result) {
            modelAndView.setViewName("successfullConfirm");
        }   else {
            modelAndView.setViewName("unSuccessfullConfirm");

        }
        return modelAndView;
    }

}
