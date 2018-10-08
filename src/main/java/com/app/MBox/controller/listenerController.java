package com.app.MBox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/listener")
public class listenerController {

    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public ModelAndView showAccountPage(ModelAndView modelAndView) {

        modelAndView.setViewName("listenerAccount");
        return modelAndView;
    }

}
