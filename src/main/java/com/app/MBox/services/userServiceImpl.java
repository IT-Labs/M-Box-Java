package com.app.MBox.services;


import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.enumeration.emailTemplateEnum;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.common.properties;
import com.app.MBox.core.repository.artistRepository;
import com.app.MBox.dto.*;
import com.app.MBox.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private userRolesService userRolesServiceImpl;
    @Autowired
    private userRepository userRepository ;

    @Autowired
    private verificationTokenService verificationTokenServiceImpl;

    @Autowired
    private roleService roleServiceImpl;

    @Autowired
    private emailTemplateService emailTemplateServiceImpl;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private properties properties;
    @Autowired
    private recordLabelArtistsService recordLabelArtistsServiceImpl;
    @Autowired
    private recordLabelService recordLabelServiceImpl;
    @Autowired
    private emailService emailService;
    @Autowired
    private artistRepository artistRepository;
    @Autowired
    private artistService artistServiceImpl;
    @Autowired
    private springChecks springChecks;

    public static int RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE=0;
    public static int ARTIST_LAZY_LOAD_INITIAL_PAGE=0;
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
            String appUrl=String.format("%s%s",properties.getConfirmUrl(),verificationToken.getToken());
            emailBodyDto emailBodyDto=parsingEmailBody(user,appUrl,emailTemplateEnum.verificationMail.toString());
            emailService.setEmail(emailBodyDto,user);
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
            String appUrl=String.format("%s%s",properties.getResetPasswordUrl(),verificationToken.getToken());
            emailBodyDto emailBodyDto=parsingEmailBody(user,appUrl,emailTemplateEnum.forgotPassword.toString());
            emailService.setEmail(emailBodyDto,user);
             return true;

        }

        return false;

    }

    public emailBodyDto parsingEmailBody(users user, String appUrl, String templateName) {
        emailTemplate emailTemplate= emailTemplateServiceImpl.findByName(templateName);
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

    public List<recordLabelDto> findRecordLabels(Pageable pageable) {
        List<recordLabelDto> recordLabelDtos=new LinkedList<>();
        List<users> users=userRepository.findRecordLabels(pageable);
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

    public List<artistDto> findArtists(int userId,Pageable pageable) {
        List<artistDto> artistsDto=new LinkedList<>();
        recordLabel recordLabel=recordLabelServiceImpl.findByUserId(userId);
        List<artist> artists=artistServiceImpl.findAllArtists(recordLabel.getId(),pageable);
        for(int i=0 ; i<artists.size();i++) {
            artistDto artistDto=new artistDto();
            artistDto.setEmail(artists.get(i).getUser().getEmail());
            artistDto.setName(artists.get(i).getUser().getName());
            if(artists.get(i).getUser().getPicture()!=null) {
                //logic for the picture from s3
            }   else {
                    artistDto.setPictureUrl(properties.getArtistDefaultPicture());
            }
            artistsDto.add(artistDto);
        }
        return artistsDto;
    }


    public List<recordLabelDto> findAndSortRecordLabels(String sortParam,int page,int size,int direction) {
        List<recordLabelDto> recordLabelDtos=new LinkedList<>();
        List<users> users;
        if(!sortParam.equals("number")) {
            if (direction == 0) {
                users = userRepository.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE, page*size+size, Sort.Direction.DESC, sortParam));
            } else {
                users = userRepository.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE, page*size+size, Sort.Direction.ASC, sortParam));
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
                users = userRepository.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE, page*size+size));
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


    public List<artistDto> findAndSortArtists(Pageable pageable) {

        List<artistDto> artistDtos=new LinkedList<>();
        recordLabel recordLabel= springChecks.getLoggedInRecordLabel();
        List<users> artists;
            artists = userRepository.findArtists(recordLabel.getId(), pageable);


        for(int i=0 ; i<artists.size();i++) {
            artistDto artistDto=new artistDto();
            artistDto.setEmail(artists.get(i).getEmail());
            artistDto.setName(artists.get(i).getName());
            if(artists.get(i).getPicture()!=null) {
                //logic for the picture from s3
            }   else {
                artistDto.setPictureUrl(properties.getArtistDefaultPicture());
            }
            artistDtos.add(artistDto);

        }
        return artistDtos;
    }

    public List<artistDto> searchArtists(String searchParam) {
        recordLabel recordLabel= springChecks.getLoggedInRecordLabel();
        List<users> artists=userRepository.searchArtists(recordLabel.getId(),searchParam);
        List<artistDto> artistDtos=new LinkedList<>();
        for(int i=0 ; i<artists.size();i++) {
            artistDto artistDto=new artistDto();
            artistDto.setEmail(artists.get(i).getEmail());
            artistDto.setName(artists.get(i).getName());

            if(artists.get(i).getPicture()!=null) {
                //logic for the picture from s3
            }   else {
                artistDto.setPictureUrl(properties.getArtistDefaultPicture());
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


}

