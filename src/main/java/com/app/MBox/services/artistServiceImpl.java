package com.app.MBox.services;

import com.app.MBox.aditional.*;
import com.app.MBox.core.model.*;
import com.app.MBox.core.repository.artistRepository;
import com.app.MBox.dto.csvErrorsDto;
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
    userServiceImpl userServiceImpl;
    @Autowired
    springProfileCheck springProfileCheck;
    @Autowired
    com.app.MBox.services.recordLabelArtistsServiceImpl recordLabelArtistsServiceImpl;
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

    public csvErrorsDto parseCsv(MultipartFile file, HttpServletRequest request) throws Exception {
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
            if(artistsDetails.length<2 && artistsDetails.length>2) {
                invalidFormatDetectedRows=String.format("%srow%d",invalidFormatDetectedRows,counterRows);
                hasErrors=true;
                invalidFormatRows=true;
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
        csvErrorsDto errors=new csvErrorsDto();
        recordLabel recordLabel=springProfileCheck.getAuthenticatedUser();
        int number=recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId());
        if(lines.size() + number>50) {
            errors.setArtistLimitExceded("Artist limit (50) exceeded");
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

            errors.setErrorFlag(true);
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
        errors.setErrorFlag(false);
        return errors;
    }
}
