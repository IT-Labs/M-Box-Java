package com.app.MBox.controller;


import com.app.MBox.aditional.passwordChecker;
import com.app.MBox.aditional.properties;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.services.recordLabelServiceImpl;
import com.app.MBox.services.userServiceImpl;
import com.app.MBox.services.verificationTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin")
public class adminController  {

    @Autowired
    userServiceImpl userServiceImpl;
    @Autowired
    recordLabelServiceImpl recordLabelServiceImpl;
    @Autowired
    verificationTokenServiceImpl verificationTokenServiceImpl;
    @Autowired
    passwordChecker passwordChecker;
    @Autowired
    properties properties;


    @RequestMapping(value = "/dashboard" , method = RequestMethod.GET)
    public ModelAndView showAdminDashboard(Model model) {
    List<recordLabelDto> recordLabels=new LinkedList<>();
    recordLabels=userServiceImpl.findRecordLabels(0,20);
    model.addAttribute("recordLabels",recordLabels);
    ModelAndView modelAndView=new ModelAndView();
    modelAndView.setViewName("adminDashboard");
    return modelAndView;
}

    @RequestMapping(value = "/lazyLoad",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processLazyLoading(@RequestParam int page) {
        List<recordLabelDto> recordLabels=new LinkedList<>();
        recordLabels=userServiceImpl.findRecordLabels(page,20);
        return recordLabels;
    }

    @RequestMapping(value = "/addNewRecordLabel",method = RequestMethod.GET)
    public ModelAndView showAddNewRecordLabelForm() {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("addNewRecordLabel");
        return modelAndView;
    }


    @RequestMapping(value = "/addNewRecordLabel" , method = RequestMethod.POST)
    public ModelAndView processAddNewRecordLabelForm(ModelAndView modelAndView,@RequestParam("name") String recordLabelName,@RequestParam("email") String recordLabelEmail,HttpServletRequest request) {
        System.out.println("NAME : " + recordLabelName + " EMAIL : " + recordLabelEmail);
        if(recordLabelName.length()<2 || recordLabelName.length()>50) {
            modelAndView.addObject("errorNameMessage","Name must be between 2 and 50 characters");
            modelAndView.setViewName("addNewRecordLabel");
            return modelAndView;
        }

        if(recordLabelEmail.length()>320) {
            modelAndView.addObject("errorNameMessage","Invalid email");
            modelAndView.setViewName("addNewRecordLabel");
            return modelAndView;
        }
        modelAndView.setViewName("confirmationAddNewRecordLabel");
        recordLabelServiceImpl.createRecordLabel(recordLabelName,recordLabelEmail,request);
        return modelAndView;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public ModelAndView processDeleteRecordLabel(ModelAndView modelAndView,@RequestParam("email") String email) {
        recordLabelServiceImpl.deleteRecordLabel(email);
        modelAndView.setViewName("redirect:dashboard");
        return modelAndView;
        }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    public String processDeleteRecordLabelGet(@RequestParam("email") String email) {
        recordLabelServiceImpl.deleteRecordLabel(email);
        return "OK";
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processSearch(@RequestParam String searchParam) {
        List<recordLabelDto> recordLabels=new LinkedList<>();
           if(!searchParam.equals("")) {
               recordLabels = userServiceImpl.search(searchParam);
               return recordLabels;
           }    else {
               return null;
           }


    }




}
