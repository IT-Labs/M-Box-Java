package com.app.MBox.services;


import com.app.MBox.aditional.*;
import com.app.MBox.core.repository.artistRepository;
import com.app.MBox.dto.*;
import com.app.MBox.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.app.MBox.core.repository.userRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
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
    private com.app.MBox.services.recordLabelArtistsServiceImpl recordLabelArtistsServiceImpl;
    @Autowired
    private recordLabelServiceImpl recordLabelServiceImpl;
    @Autowired
    private emailService emailService;
    @Autowired
    private artistRepository artistRepository;
    @Autowired
    private artistServiceImpl artistServiceImpl;
    @Autowired
    private springChecks springChecks;


    public users findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public users saveUser(users user) {

        return userRepository.save(user);
    }

    public void delete (users user) {
        userRepository.delete(user);
    }



    public users registerNewUserAccount(userDto accountDto,HttpServletRequest request) throws emailAlreadyExistsException {


            if(findByEmail(accountDto.getEmail())!=null) {
                throw new emailAlreadyExistsException("Email already exists");
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
        emailBodyDto emailBodyDto=new emailBodyDto();
        if(springChecks.isProductionProfile()) {
            emailBodyDto.setBody(body);
        }   else {
            String qaDevBody=String.format("%s <h1> Sent from email %s </h1>",body,user.getEmail());
            emailBodyDto.setBody(qaDevBody);
        }
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
            int number=recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
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

    public List<artistDto> findArtists(int userId,int page) {
        List<artistDto> artistsDto=new LinkedList<>();
        recordLabel recordLabel=recordLabelServiceImpl.findByUserId(userId);
        List<artist> artists=artistServiceImpl.findAllArtists(recordLabel.getId(),PageRequest.of(page,20));
        for(int i=0 ; i<artists.size();i++) {
            artistDto artistDto=new artistDto();
            artistDto.setEmail(artists.get(i).getUser().getEmail());
            artistDto.setName(artists.get(i).getUser().getName());
            if(artists.get(i).getUser().getPicture()!=null) {
                //logic for the picture from s3
            }   else {
                artistDto.setPictureUrl("data:image/png;base64,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");
            }
            artistsDto.add(artistDto);
        }
        return artistsDto;
    }


    public List<recordLabelDto> findAndSortRecordLabels(String sortParam,int page,int direction) {
        List<recordLabelDto> recordLabelDtos=new LinkedList<>();
        List<users> users;
        if(!sortParam.equals("number")) {
            if (direction == 0) {
                users = userRepository.findRecordLabels(PageRequest.of(0, page*20+20, Sort.Direction.DESC, sortParam));
            } else {
                users = userRepository.findRecordLabels(PageRequest.of(0, page*20+20, Sort.Direction.ASC, sortParam));
            }
            for (int i = 0; i < users.size(); i++) {
                recordLabelDto recordLabelDto = new recordLabelDto();
                recordLabelDto.setEmail(users.get(i).getEmail());
                recordLabelDto.setName(users.get(i).getName());
                recordLabel recordLabel = recordLabelServiceImpl.findByUserId(users.get(i).getId());
                int number = recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
                recordLabelDto.setNumber(number);
                recordLabelDtos.add(recordLabelDto);
            }
        }   else {
                users = userRepository.findRecordLabels(PageRequest.of(0, page*20+20));
                for (int i = 0; i < users.size(); i++) {
                    recordLabelDto recordLabelDto = new recordLabelDto();
                    recordLabelDto.setEmail(users.get(i).getEmail());
                    recordLabelDto.setName(users.get(i).getName());
                    recordLabel recordLabel = recordLabelServiceImpl.findByUserId(users.get(i).getId());
                    int number = recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
                    recordLabelDto.setNumber(number);
                    recordLabelDtos.add(recordLabelDto);
                }

                if(direction==0) {
                    Collections.sort(recordLabelDtos,Collections.reverseOrder());
                }   else {
                    Collections.sort(recordLabelDtos);
                }


        }
        return recordLabelDtos;
    }


    public List<artistDto> findAndSortArtists(String sortParam, int page, int direction) {

        List<artistDto> artistDtos=new LinkedList<>();
        recordLabel recordLabel= springChecks.getAuthenticatedUser();
        List<users> artists;
        if(direction==0) {
           artists = userRepository.findArtists(recordLabel.getId(), PageRequest.of(0, page * 20 + 20, Sort.Direction.DESC, sortParam));
        }  else {
            artists = userRepository.findArtists(recordLabel.getId(), PageRequest.of(0, page * 20 + 20, Sort.Direction.ASC, sortParam));
        }

        for(int i=0 ; i<artists.size();i++) {
            artistDto artistDto=new artistDto();
            artistDto.setEmail(artists.get(i).getEmail());
            artistDto.setName(artists.get(i).getName());
            if(artists.get(i).getPicture()!=null) {
                //logic for the picture from s3
            }   else {
                artistDto.setPictureUrl("data:image/png;base64,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");
            }
            artistDtos.add(artistDto);

        }
        return artistDtos;
    }

    public List<artistDto> searchArtists(String searchParam) {
        recordLabel recordLabel= springChecks.getAuthenticatedUser();
        List<users> artists=userRepository.searchArtists(recordLabel.getId(),searchParam);
        List<artistDto> artistDtos=new LinkedList<>();
        for(int i=0 ; i<artists.size();i++) {
            artistDto artistDto=new artistDto();
            artistDto.setEmail(artists.get(i).getEmail());
            artistDto.setName(artists.get(i).getName());

            if(artists.get(i).getPicture()!=null) {
                //logic for the picture from s3
            }   else {
                artistDto.setPictureUrl("data:image/png;base64,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");
            }
            artistDtos.add(artistDto);
        }

        return artistDtos;
    }

    public void setUserPassword(String token, String password) {
        users user=verificationTokenServiceImpl.findByToken(token);
        user.setActivated(true);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        saveUser(user);
        verificationTokenServiceImpl.delete(token);
    }


    public String getView(String token) {
         users user=verificationTokenServiceImpl.findByToken(token);
         artist artist=artistServiceImpl.findByUserId(user.getId());
         if(artist!=null) {
             return "artistAccount";
         }  else {
             return "recordLabelDashboard";
         }
    }
}

