package com.app.MBox.controller;


import com.app.MBox.config.properties;
import com.app.MBox.dto.listenerDto;
import com.app.MBox.services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/listener")
public class listenerController {

    @Autowired
    private userService userService;

    @Autowired
    private properties properties;

    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public ModelAndView showAccountPage(ModelAndView modelAndView, Model model) {
        listenerDto listener=userService.getListener();
        listenerDto listenerDto=new listenerDto();
        model.addAttribute("listener",listener);
        modelAndView.setViewName("listenerAccount");
        return modelAndView;
    }

    @RequestMapping(value = "/account",method = RequestMethod.POST)
    public ModelAndView processAccountPage(ModelAndView modelAndView,@ModelAttribute("listenerDto") listenerDto listenerDto) {
        userService.saveListener(listenerDto);
        modelAndView.setViewName("listenerAccount");
        return modelAndView;
    }

    @RequestMapping(value = "/picture",method = RequestMethod.POST)
    public ModelAndView processArtistPicture(ModelAndView modelAndView, @RequestParam("file") MultipartFile file, @RequestParam("id") int id) {
        String result=userService.addListenerPicture(file,id);
        if(result.equals("wrongFormat")) {
            modelAndView.addObject(result, properties.getImageExtensionError());

        } else if (result.equals("sizeExceeded")) {
            modelAndView.addObject(result,properties.getMaxUploadImageSize());

        }

        modelAndView.setViewName("redirect:account");
        return modelAndView;
    }


}
