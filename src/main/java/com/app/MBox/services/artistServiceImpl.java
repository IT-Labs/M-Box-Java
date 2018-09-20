package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.enumeration.emailTemplateEnum;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.common.properties;
import com.app.MBox.core.model.*;
import com.app.MBox.core.repository.artistRepository;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.csvParseResultDto;
import com.app.MBox.dto.emailBodyDto;
import com.app.MBox.dto.sendEmailDto;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@Service
public class artistServiceImpl implements artistService {

    @Autowired
    artistRepository artistRepository;
    @Autowired
    userService userServiceImpl;
    @Autowired
    springChecks springChecks;
    @Autowired
    recordLabelArtistsService recordLabelArtistsServiceImpl;
    @Autowired
    roleService roleServiceImpl;
    @Autowired
    verificationTokenService verificationTokenServiceImpl;
    @Autowired
    properties properties;
    @Autowired
    emailService emailService;
    @Autowired
    userRolesService userRolesServiceImpl;
    @Autowired
    emailTemplateService emailTemplateServiceImpl;


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
            throw new emailAlreadyExistsException(properties.getEmailAlreadyExistsMessage());
        }
        recordLabel recordLabel= springChecks.getLoggedInRecordLabel();
        int number=recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
        if(number>=properties.getArtistLimit()) {
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
        String appUrl=String.format("%s%s",properties.getJoinUrl(),verificationToken.getToken());
        emailBodyDto emailBodyDto=userServiceImpl.parsingEmailBody(user,appUrl, emailTemplateEnum.artistSignUpMail.toString());
        emailService.setAndSendEmail(emailBodyDto,user);
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

    public csvParseResultDto addArtistsByCsvFile(MultipartFile file, HttpServletRequest request) throws Exception {
        BufferedReader reader=new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        String invalidFormatDetectedRows="Invalid format detected (has to be: Artist Email, Artist Name), row(s):";
        String invalidEmailFormatDetectedRows="Invalid Email format (example@example.com), row(s):";
        String maxLengthOfEmailRows="Max length of email (320) exceeded, row(s):";
        String maxLengthOfArtistNameRows="Max length of Artist Name (50) exceeded, row(s):";
        int counterRows=1;
        boolean invalidFormatRows=false;
        boolean invalidEmailFormat=false;
        boolean maxLengthOfEmail=false;
        boolean maxLengthOfArtistName=false;
        boolean hasErrors=false;
        List<String> lines=new LinkedList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
            String [] artistsDetails=line.split(",");
            if(artistsDetails.length<2 || artistsDetails.length>2) {
                invalidFormatDetectedRows=String.format("%srow%d",invalidFormatDetectedRows,counterRows);
                hasErrors=true;
                invalidFormatRows=true;
                counterRows++;
                continue;
            }

            if(!EmailValidator.getInstance().isValid(artistsDetails[0])) {
                invalidEmailFormatDetectedRows=String.format("%srow%d",invalidEmailFormatDetectedRows,counterRows);
                invalidEmailFormat=true;
                hasErrors=true;
            }

            if(artistsDetails[0].length()>320) {
                maxLengthOfEmailRows=String.format("%srow%d",maxLengthOfEmailRows,counterRows);
                hasErrors=true;
                maxLengthOfArtistName=true;
            }

            if(artistsDetails[1].length()>50) {
                maxLengthOfArtistNameRows=String.format("%srow%d, ",maxLengthOfArtistNameRows,counterRows);
                hasErrors=true;
                maxLengthOfEmail=true;
            }
            counterRows++;
        }
        csvParseResultDto errors=new csvParseResultDto();
        recordLabel recordLabel= springChecks.getLoggedInRecordLabel();
        int number=recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
        if(lines.size() + number>properties.getArtistLimit()) {
            errors.setArtistLimitExceded(properties.getArtistNumberMessage());
            hasErrors=true;
        }



        if(hasErrors) {
            if(invalidEmailFormat) {
                errors.setInvalidEmailFormatDetectedRows(invalidEmailFormatDetectedRows);
            }
            if(invalidFormatRows) {
                errors.setInvalidFormatDetectedRows(invalidFormatDetectedRows);
            }
            if(maxLengthOfArtistName) {
                errors.setMaxLengthOfArtistNameRows(maxLengthOfArtistNameRows);
            }
            if(maxLengthOfEmail) {
                errors.setMaxLengthOfEmailRows(maxLengthOfEmailRows);
            }

            errors.setHasErrors(true);
            return errors;
        }

    if(reader!=null) {
        reader.close();
    }
    int artistAdded=0;
        for (String artist:lines) {
            String [] artistsDetails=artist.split(",");
               try {
                   artistAdded++;
                   inviteArtist(artistsDetails[1], artistsDetails[0], request);
               } catch (emailAlreadyExistsException e) {
                   artistAdded--;
                    continue;
               }

        }

        errors.setArtistAdded(artistAdded);
        errors.setNumber(number);
        errors.setHasErrors(false);
        return errors;
    }

    public void deleteArtist(String email) {
        users user=userServiceImpl.findByEmail(email);
        if(user==null) {
            return;
        }
        artist artist=artistRepository.findByUserId(user.getId());
        if(artist==null || artist.isDeleted()) {
            return;
        }

        artist.setDeleted(true);
        emailService.sendDeleteArtistEmail(user);
        save(artist);
        recordLabelArtists recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(artist.getId());
        if(recordLabelArtists!=null) {
            recordLabelArtistsServiceImpl.delete(recordLabelArtists);
        }
    }

    public List<artistDto> findRecentlyAddedArtist(){
        List<artist>artists=artistRepository.findRecentlyAddedArtist();
        List<artistDto> artistDtos=new LinkedList<>();
        for (artist a:artists) {
            artistDto artistDto=new artistDto();
            artistDto.setName(a.getUser().getName());
            artistDto.setDeleted(a.isDeleted());
            recordLabelArtists recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(a.getId());
            if(!a.isDeleted()) {
                artistDto.setRecordLabelName(recordLabelArtists.getRecordLabel().getUser().getName());
            }
            if(a.getUser().getPicture()!=null) {
                //logic from s3 for the picture
            }   else {
                artistDto.setPictureUrl(properties.getArtistDefaultPicture());
            }
            artistDtos.add(artistDto);
        }

        return artistDtos;
    }
}
