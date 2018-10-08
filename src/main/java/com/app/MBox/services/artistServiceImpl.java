package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.enumeration.emailTemplateEnum;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.config.properties;
import com.app.MBox.core.model.*;
import com.app.MBox.core.repository.artistRepository;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.csvParseResultDto;
import com.app.MBox.dto.emailBodyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
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
    @Lazy
    emailService emailServiceImpl;
    @Autowired
    userRolesService userRolesServiceImpl;
    @Autowired
    amazonS3ClientService amazonS3ClientService;
    @Autowired
    configurationService configurationService;
    @Autowired
    songService songService;


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
        emailServiceImpl.setAndSendEmail(emailBodyDto,user);
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
                maxLengthOfEmail=true;

            }

            if(artistsDetails[1].length()>50) {
                maxLengthOfArtistNameRows=String.format("%srow%d, ",maxLengthOfArtistNameRows,counterRows);
                hasErrors=true;
                maxLengthOfArtistName=true;
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
        emailServiceImpl.sendDeleteArtistEmail(user);
        save(artist);
        recordLabelArtists recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(artist.getId());
        if(recordLabelArtists!=null) {
            recordLabelArtistsServiceImpl.delete(recordLabelArtists);
        }
    }

    public List<artistDto> findRecentlyAddedArtist(Pageable pageable){
        List<artist>artists=artistRepository.findRecentlyAddedArtist(pageable);
        List<artistDto> artistDtos=mapArtistToArtistDto(artists);
        return artistDtos;
    }

    public List<artistDto> findAllArtists(Pageable pageable) {
        List<users> artists=userServiceImpl.findAllRecentlyAddedArtists(pageable);

        List<artistDto> artistDtos=userServiceImpl.mapUserToArtistDto(artists);
        return artistDtos;

    }


    public List<artistDto> mapArtistToArtistDto(List<artist> artists) {

        List<artistDto> artistDtos=artists.stream().map(artist ->{
            artistDto artistDto=new artistDto();
            artistDto.setId(artist.getId());
            artistDto.setBio(artist.getBio());
            users artistUser=artist.getUser();
            artistDto.setName(artistUser.getName());
            artistDto.setDeleted(artist.isDeleted());
            artistDto.setEmail(artistUser.getEmail());
            if(artist.getUser().getDateOfBirth()!=null) {
                String pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(artist.getUser().getDateOfBirth());
                artistDto.setDateOfBirth(date);
            }
            recordLabelArtists recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(artist.getId());
            if(!artist.isDeleted()) {
                artistDto.setRecordLabelName(recordLabelArtists.getRecordLabel().getUser().getName());
                artistDto.setRecordLabelId(recordLabelArtists.getRecordLabel().getId());
            }
            if(artist.getUser().getPicture()!=null) {
                artistDto.setPictureUrl(amazonS3ClientService.getPictureUrl(artistUser.getPicture()));
            }   else {
                artistDto.setPictureUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getArtistDefaultPicture()).getValue()));
            }
            return artistDto;
        }).collect(Collectors.toList());
        return artistDtos;

    }

    public artist findById(int id) {
           return artistRepository.findById(id);
    }

    public String addPicture(MultipartFile file,int id) {
        String result=songService.isValidPicture(file);
        if(result.equals("OK")) {
            artist artist=findById(id);
            String[] extension = file.getContentType().split("/");
            String imageName = String.format("%s.%s", UUID.randomUUID().toString(),extension[1]);
            amazonS3ClientService.uploadFileToS3Bucket(file, false, imageName);
            artist.getUser().setPicture(imageName);
            save(artist);
        }

        return result;
    }

    public void saveArtist(artistDto artistDto) {
        artist artist=findById(artistDto.getId());
        artist.setBio(artistDto.getBio());
        artist.getUser().setName(artistDto.getName());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(artistDto.getDateOfBirth());
            artist.getUser().setDateOfBirth(date);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
       save(artist);
    }
}
