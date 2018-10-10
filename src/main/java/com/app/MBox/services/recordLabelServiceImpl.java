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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Autowired
    songService songService;

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
            Optional<recordLabelArtists> recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(artist.getId());
            if(recordLabelArtists.isPresent()) {
                recordLabelArtistsServiceImpl.delete(recordLabelArtists.get());
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
        Page<users> recordLabels=userServiceImpl.findAllActiveRecordLabels(pageable);
        List<recordLabelDto> recordLabelsDto=userServiceImpl.mapUserToRecordLabelDto(recordLabels.getContent());
        return recordLabelsDto;
    }

    public List<recordLabelDto> getAllRecordLabels() {
        List<users> recordLabels=userServiceImpl.findAllActiveRecordLabels();
        List<recordLabelDto> recordLabelDtos=userServiceImpl.mapUserToRecordLabelDto(recordLabels);
        return recordLabelDtos;
    }

    public Optional<recordLabel> findById(int id) {
       return  recordLabelRepository.findById(id);
    }

    public  recordLabelDto findRecordLabel(int id) {
        Optional<recordLabel> recordLabel=findById(id);
        recordLabelDto recordLabelDto = new recordLabelDto();
        if(recordLabel.isPresent()) {
            recordLabelDto.setAboutInfo(recordLabel.get().getAboutInfo());
            recordLabelDto.setId(recordLabel.get().getId());
            users user = recordLabel.get().getUser();
            recordLabelDto.setName(user.getName());
            recordLabelDto.setEmail(user.getEmail());
            if (user.getPicture() != null) {
                recordLabelDto.setPictureUrl(amazonS3ClientService.getPictureUrl(user.getPicture()));
            } else {
                recordLabelDto.setPictureUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getRecordLabelDefaultPicture()).getValue()));
            }

        }
        return recordLabelDto;
    }

   public void saveRecordLabel(recordLabelDto recordLabelDto) {

        Optional<recordLabel> recordLabel=findById(recordLabelDto.getId());
        if(recordLabel.isPresent()) {
            recordLabel.get().setAboutInfo(recordLabelDto.getAboutInfo());
            recordLabel.get().getUser().setName(recordLabelDto.getName());
            saveRecordLabel(recordLabel.get());
        }
   }

   public String addPicture(MultipartFile file, int id) {

       String result=songService.isValidPicture(file);
       Optional<recordLabel> recordLabel=findById(id);
       if(result.equals("OK") && recordLabel.isPresent() && !file.isEmpty()) {
           String[] extension = file.getContentType().split("/");
           String imageName = String.format("%s.%s", UUID.randomUUID().toString(),extension[1]);
           amazonS3ClientService.uploadFileToS3Bucket(file, false, imageName);
           recordLabel.get().getUser().setPicture(imageName);
           saveRecordLabel(recordLabel.get());
       }

       return result;

   }
}
