package com.app.MBox.services;


import com.app.MBox.aditional.emailTemplateEnum;
import com.app.MBox.aditional.properties;
import com.app.MBox.dto.emailBodyDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.dto.sendEmailDto;
import com.app.MBox.dto.userDto;
import com.app.MBox.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.app.MBox.core.repository.userRepository;
import com.app.MBox.aditional.rolesEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Service("userServiceImpl")
public class userServiceImpl implements userService {


    @Autowired
    private userRolesServiceImpl userRolesServiceImpl;
    @Autowired
    private userRepository userRepository ;

    @Autowired
    private verificationTokenServiceImpl verificationTokenServiceImpl;

    @Autowired
    private roleServiceImpl roleServiceImpl;

    @Autowired
    private emailTemplateService emailTemplateService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private properties properties;
    @Autowired
    private recordLabelArtistsServiceServiceImpl recordLabelArtistsServiceImpl;
    @Autowired
    private recordLabelServiceImpl recordLabelServiceImpl;
    @Autowired
    private emailService emailService;

    public users findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public users saveUser(users user) {

        return userRepository.save(user);
    }




    public users registerNewUserAccount(userDto accountDto,HttpServletRequest request) throws Exception {


            if(findByEmail(accountDto.getEmail())!=null) {
                throw new Exception("Email already exists");
            }
            role role= roleServiceImpl.findByName(rolesEnum.LISTENER.toString());
            users user=createUser(accountDto,role);
            verificationToken verificationToken=verificationTokenServiceImpl.createToken(user);
            String appUrl=String.format("%s://%s%sconfirm?token=%s",request.getScheme(),request.getServerName(),properties.getPORT(),verificationToken.getToken());
            emailBodyDto emailBodyDto=parsingEmailBody(user,appUrl,emailTemplateEnum.verificationMail.toString());
            sendEmailDto sendEmail=new sendEmailDto();
            sendEmail.setBody(emailBodyDto.getBody());
            sendEmail.setSubject(emailBodyDto.getSubject());
            sendEmail.setFromUserFullName(user.getName());
            sendEmail.setToEmail(user.getEmail());
            emailService.sendMail(sendEmail);

        return user;
    }

    public users createUser(userDto accountDto,role role) {
        users user=new users();
        user.setName(accountDto.getName());
        user.setEmail(accountDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
        user.setActivated(false);
        user=saveUser(user);
        userRoles userRoles=new userRoles();
        userRoles.setUser(user);
        userRoles.setRole(role);
        userRolesServiceImpl.saveUserRoles(userRoles);
        return user;

    }



    public void savePassword(String password, String token) {
            users user=verificationTokenServiceImpl.findByToken(token);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            saveUser(user);
            verificationTokenServiceImpl.delete(token);
    }


    public boolean forgotPassword(String email,HttpServletRequest request) {
        users user=findByEmail(email);
        if(user!=null && user.isActivated()) {
            verificationToken verificationToken=verificationTokenServiceImpl.createToken(user);
            String appUrl=String.format("%s://%s%sresetPassword?token=%s",request.getScheme(),request.getServerName(),properties.getPORT(),verificationToken.getToken());
            emailBodyDto emailBodyDto=parsingEmailBody(user,appUrl,emailTemplateEnum.forgotPassword.toString());
            sendEmailDto sendEmail=new sendEmailDto();
            sendEmail.setBody(emailBodyDto.getBody());
            sendEmail.setSubject(emailBodyDto.getSubject());
            sendEmail.setFromUserFullName(user.getName());
            sendEmail.setToEmail(user.getEmail());
            emailService.sendMail(sendEmail);
             return true;

        }

        return false;

    }

    public emailBodyDto parsingEmailBody(users user, String appUrl, String templateName) {
        emailTemplate emailTemplate=emailTemplateService.findByName(templateName);
        String newBody=emailTemplate.getBody().replace(properties.getNAME(),user.getName());
        String newBody1=newBody.replace(properties.getEMAILADRESS(),user.getEmail());
        String body=newBody1.replace(properties.getAPPURL(),appUrl);
        List <String> list=new LinkedList<>();
        emailBodyDto emailBodyDto=new emailBodyDto();
        emailBodyDto.setBody(body);
        emailBodyDto.setSubject(emailTemplate.getSubject());
        return emailBodyDto;
    }

    public List<recordLabelDto> findRecordLabels(int page,int size) {
        List<recordLabelDto> recordLabelDtos=new LinkedList<>();
        List<users> users=userRepository.findRecordLabels(PageRequest.of(page,size));
        for(int i=0 ; i<users.size() ; i++) {
            recordLabelDto recordLabelDto=new recordLabelDto();
            recordLabelDto.setEmail(users.get(i).getEmail());
            recordLabelDto.setName(users.get(i).getName());
            recordLabel recordLabel=recordLabelServiceImpl.findByUserId(users.get(i).getId());
            int number= recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
            recordLabelDto.setNumber(number);
            recordLabelDtos.add(recordLabelDto);
        }
        return recordLabelDtos;
    }


    public List<recordLabelDto> search(String searchParam) {
        List<recordLabelDto> recordLabelDtos=new LinkedList<>();
        List<users> users=userRepository.searchRecordLabels(searchParam);
        for(int i=0 ; i<users.size() ; i++) {
            recordLabelDto recordLabelDto=new recordLabelDto();
            recordLabelDto.setEmail(users.get(i).getEmail());
            recordLabelDto.setName(users.get(i).getName());
            recordLabel recordLabel=recordLabelServiceImpl.findByUserId(users.get(i).getId());
            int number=recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
            recordLabelDto.setNumber(number);
            recordLabelDtos.add(recordLabelDto);
        }

        return recordLabelDtos;
    }

}

