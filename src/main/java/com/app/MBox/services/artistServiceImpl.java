package com.app.MBox.services;

import com.app.MBox.aditional.*;
import com.app.MBox.core.model.*;
import com.app.MBox.core.repository.artistRepository;
import com.app.MBox.dto.emailBodyDto;
import com.app.MBox.dto.sendEmailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class artistServiceImpl implements artistService {

    @Autowired
    artistRepository artistRepository;
    @Autowired
    userServiceImpl userServiceImpl;
    @Autowired
    springProfileCheck springProfileCheck;
    @Autowired
    recordLabelArtistsServiceServiceImpl recordLabelArtistsServiceImpl;
    @Autowired
    roleServiceImpl roleServiceImpl;
    @Autowired
    verificationTokenServiceImpl verificationTokenServiceImpl;
    @Autowired
    properties properties;
    @Autowired
    emailService emailService;
    @Autowired
    userRolesServiceImpl userRolesServiceImpl;


    @Override
    public List<artist> findAllArtists(int recordLabelId) {
        return artistRepository.findAllArtists(recordLabelId);
    }

    public List<artist> findAllArtists(int recordLabelId, Pageable pageable) {
        return artistRepository.findAllArtists(recordLabelId,pageable);
    }

    public void save(artist artist) {
        artistRepository.save(artist);
    }

    public artist findByUserId(int userId) {
        return artistRepository.findByUserId(userId);
    }

    public users inviteArtist(String name, String email, HttpServletRequest request) throws emailAlreadyExistsException {
        users user=userServiceImpl.findByEmail(email);
        if(user!=null) {
            throw new emailAlreadyExistsException("email already exists");
        }
        recordLabel recordLabel=springProfileCheck.getAuthenticatedUser();
        int number=recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
        if(number>49) {
            return null;
        }
        user=createUser(name,email);
        artist artist=new artist();
        artist.setUser(user);
        artist.setDeleted(false);
        save(artist);
        recordLabelArtists recordLabelArtists=new recordLabelArtists();
        recordLabelArtists.setArtist(artist);
        recordLabelArtists.setRecordLabel(recordLabel);
        recordLabelArtistsServiceImpl.save(recordLabelArtists);
        verificationToken verificationToken=verificationTokenServiceImpl.createToken(user);

        String appUrl=String.format("%s://%s%sjoinIfInvited?token=%s",request.getScheme(),request.getServerName(),properties.getPORT(),verificationToken.getToken());
        emailBodyDto emailBodyDto=userServiceImpl.parsingEmailBody(user,appUrl, emailTemplateEnum.artistSignUpMail.toString());
        sendEmailDto sendEmail=new sendEmailDto();
        sendEmail.setBody(emailBodyDto.getBody());
        sendEmail.setSubject(emailBodyDto.getSubject());
        sendEmail.setFromUserFullName(user.getName());
        sendEmail.setToEmail(user.getEmail());
        emailService.sendMail(sendEmail);
        return user;
    }

    public users createUser(String name,String email) {
        users user=new users();
        user.setActivated(false);
        user.setEmail(email);
        user.setName(name);
        role role=roleServiceImpl.findByName(rolesEnum.ARTIST.toString());
        userRoles userRoles=new userRoles();
        user=userServiceImpl.saveUser(user);
        userRoles.setRole(role);
        userRoles.setUser(user);
        userRolesServiceImpl.saveUserRoles(userRoles);
        return user;
    }
}
