package com.app.MBox.controller;


import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.config.properties;
import com.app.MBox.common.validation.recordLabelValidator;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/admin")
public class adminController  {

    @Autowired
    userService userServiceImpl;
    @Autowired
    recordLabelService recordLabelServiceImpl;
    @Autowired
    recordLabelValidator recordLabelValidator;
    @Autowired
    properties properties;
    public static int RECORD_LABEL_LAZY_LOAD_SIZE=20;
    public static int RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE=0;


    @RequestMapping(value = "/dashboard" , method = RequestMethod.GET)
    public ModelAndView showAdminDashboard(Model model) {
    List<recordLabelDto> recordLabels=new LinkedList<>();
    recordLabels=userServiceImpl.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE,RECORD_LABEL_LAZY_LOAD_SIZE));
    model.addAttribute("recordLabels",recordLabels);
    ModelAndView modelAndView=new ModelAndView();
    modelAndView.setViewName("adminDashboard");
    return modelAndView;
}

    @RequestMapping(value = "/lazyLoad",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processLazyLoading(Pageable pageable) {
        List<recordLabelDto> recordLabels=new LinkedList<>();
        recordLabels=userServiceImpl.findRecordLabels(pageable);
        return recordLabels;
    }

    @RequestMapping(value = "/record-label",method = RequestMethod.GET)
    public ModelAndView showAddNewRecordLabelForm() {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("addNewRecordLabel");
        return modelAndView;
    }


    @RequestMapping(value = "/record-label" , method = RequestMethod.POST)
    public ModelAndView processAddNewRecordLabelForm(ModelAndView modelAndView,@RequestParam("name") String recordLabelName,@RequestParam("email") String recordLabelEmail,HttpServletRequest request) {
        if(!recordLabelValidator.isValid(recordLabelName,recordLabelEmail)) {
            modelAndView.addObject("errorNameMessage",properties.getInvalidDataMessage());
            modelAndView.setViewName("addNewRecordLabel");
            return modelAndView;
        }

        try {
            recordLabelServiceImpl.createRecordLabel(recordLabelName, recordLabelEmail, request);
            modelAndView.setViewName("confirmationAddNewRecordLabel");
            return modelAndView;
        } catch (emailAlreadyExistsException e) {
            log.error(e.getMessage());
            modelAndView.setViewName("addNewRecordLabel");
            modelAndView.addObject("emailAlreadyExistsError",properties.getEmailAlreadyExistsMessage());
            return modelAndView;
        }
    }

    @RequestMapping(value = "/delete-record-label",method = RequestMethod.GET)
    public ModelAndView processDeleteRecordLabel(ModelAndView modelAndView,@RequestParam("email") String email) {
        recordLabelServiceImpl.deleteRecordLabel(email);
        modelAndView.setViewName("redirect:dashboard");
        return modelAndView;
        }


    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processSearch(@RequestParam String searchParam) {
        List<recordLabelDto> recordLabels=new LinkedList<>();
           if(!searchParam.equals("")) {
               recordLabels = userServiceImpl.search(searchParam);
               return recordLabels;
           }    else {
               return recordLabels;
           }


    }


    @RequestMapping(value = "/sort",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processSort(@RequestParam String sortParam,@RequestParam int page,@RequestParam int size,@RequestParam int direction) {
        List<recordLabelDto> recordLabels=new LinkedList<>();
        recordLabels=userServiceImpl.findAndSortRecordLabels(sortParam,page,size,direction);
        return recordLabels;
    }



}
