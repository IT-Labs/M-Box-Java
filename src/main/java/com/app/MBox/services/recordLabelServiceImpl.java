package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.enumeration.emailTemplateEnum;
import com.app.MBox.config.properties;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.core.model.*;
import com.app.MBox.core.repository.recordLabelRepository;
import com.app.MBox.dto.emailBodyDto;
import com.app.MBox.dto.recordLabelDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service("recordLabelServiceImpl")
public class recordLabelServiceImpl implements recordLabelService {

    @Autowired
    recordLabelRepository recordLabelRepository;
    @Autowired
    userService userServiceImpl;
    @Autowired
    roleService roleServiceImpl;
    @Autowired
    userRolesService userRolesServiceImpl;
    @Autowired
    verificationTokenService verificationTokenServiceImpl;
    @Autowired
    artistService artistServiceImpl;
    @Autowired
    properties properties;
    @Autowired
    emailService emailServiceImpl;
    @Autowired
    amazonS3ClientService amazonS3ClientService;
    @Autowired
    configurationService configurationService;
    @Autowired
    recordLabelArtistsService recordLabelArtistsServiceImpl;

    public recordLabel findByUserId(int userId) {
        return recordLabelRepository.findByUserId(userId);
    }

    public recordLabel saveRecordLabel(recordLabel recordLabel) {

        return recordLabelRepository.save(recordLabel);
    }



    public recordLabel createRecordLabel(String name,String email,HttpServletRequest request) throws emailAlreadyExistsException {
        //create record label generate token and send mail
        users user=userServiceImpl.findByEmail(email);
        if(user!=null) {
            throw new emailAlreadyExistsException("record label already exists");
        }
        user=createUser(name,email);
        recordLabel recordLabel=new recordLabel();
        recordLabel.setUser(user);
        recordLabel=saveRecordLabel(recordLabel);
        verificationToken verificationToken=verificationTokenServiceImpl.createToken(user);

        String appUrl=String.format("%s%s",properties.getJoinUrl(),verificationToken.getToken());
        emailBodyDto emailBodyDto=userServiceImpl.parsingEmailBody(user,appUrl,emailTemplateEnum.recordLabelSignUp.toString());
        emailServiceImpl.setAndSendEmail(emailBodyDto,user);

        return recordLabel;
    }


    public users createUser(String name,String email) {
        // create new authenticatedUser and add role
        users user=new users();
        user.setEmail(email);
        user.setName(name);
        user.setActivated(false);
        user=userServiceImpl.saveUser(user);
        role role=roleServiceImpl.findByName(rolesEnum.RECORDLABEL.toString());
        userRoles userRoles=new userRoles();
        userRoles.setRole(role);
        userRoles.setUser(user);
        userRolesServiceImpl.saveUserRoles(userRoles);
        return user;
    }




    public void deleteRecordLabel(String email) {
        users user=userServiceImpl.findByEmail(email);
        recordLabel recordLabel=findByUserId(user.getId());
        List<artist> artists=artistServiceImpl.findAllArtists(recordLabel.getId());
        for (artist artist:artists) {
            artist.setDeleted(true);
            emailServiceImpl.sendDeleteArtistEmail(artist.getUser());
            artistServiceImpl.save(artist);
            recordLabelArtists recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(artist.getId());
            if(recordLabelArtists!=null) {
                recordLabelArtistsServiceImpl.delete(recordLabelArtists);
            }
        }
        emailServiceImpl.sendDeleteRecordLabelEmail(user);
        userRoles userRoles=userRolesServiceImpl.findByUserId(user.getId());
            if(userRoles!=null) {
                userRolesServiceImpl.deleteUserRoles(userRoles);
            }
        if(user.getPicture()!=null) {
                amazonS3ClientService.deleteFileFromS3Bucket(user.getPicture());
        }
        recordLabelRepository.delete(recordLabel);
        userServiceImpl.delete(user);

    }

    public List<recordLabelDto> getRecordLabels(Pageable pageable) {
        List<users> recordLabels=userServiceImpl.findAllActiveRecordLabels(pageable);
        List<recordLabelDto> recordLabelsDto=recordLabels.stream().map(record->{
            recordLabelDto recordLabelDto=new recordLabelDto();
            recordLabelDto.setName(record.getName());
            if(record.getPicture()!=null){
                recordLabelDto.setPictureUrl(amazonS3ClientService.getPictureUrl(record.getPicture()));
            }   else {
                recordLabelDto.setPictureUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getRecordLabelDefaultPicture()).getValue()));
            }
            return recordLabelDto;
        }).collect(Collectors.toList());
        return recordLabelsDto;
    }

    public List<recordLabelDto> getAllRecordLabels() {
        List<users> recordLabels=userServiceImpl.findAllActiveRecordLabels();
        List<recordLabelDto> recordLabelDtos=recordLabels.stream().map(record->{
            recordLabelDto recordLabelDto=new recordLabelDto();
            recordLabelDto.setName(record.getName());
            return recordLabelDto;
        }).collect(Collectors.toList());
        return recordLabelDtos;
    }
}
