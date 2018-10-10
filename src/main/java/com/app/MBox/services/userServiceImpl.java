package com.app.MBox.services;


import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.enumeration.emailTemplateEnum;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.config.properties;
import com.app.MBox.dto.*;
import com.app.MBox.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
    private emailService emailServiceImpl;
    @Autowired
    private artistService artistServiceImpl;
    @Autowired
    private springChecks springChecks;
    @Autowired
    private amazonS3ClientService amazonS3ClientService;
    @Autowired
    private configurationService configurationService;

    public static int RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE=0;
    public users findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public users saveUser(users user) {

        return userRepository.save(user);
    }

    public void delete (users user) {
        userRepository.delete(user);
    }



    public users registerNewUserAccount(userDto accountDto) throws emailAlreadyExistsException {


            if(findByEmail(accountDto.getEmail())!=null) {
                throw new emailAlreadyExistsException(properties.getEmailAlreadyExistsMessage());
            }
            role role= roleServiceImpl.findByName(rolesEnum.LISTENER.toString());
            users user=createUser(accountDto,role);
            verificationToken verificationToken=verificationTokenServiceImpl.createToken(user);
            String appUrl=String.format("%s%s",properties.getConfirmUrl(),verificationToken.getToken());
            emailBodyDto emailBodyDto=parsingEmailBody(user,appUrl,emailTemplateEnum.verificationMail.toString());
            emailServiceImpl.setAndSendEmail(emailBodyDto,user);
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
            emailServiceImpl.setAndSendEmail(emailBodyDto,user);
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
        Page<users> users=userRepository.findRecordLabels(pageable);
        List<recordLabelDto> recordLabelDtos=mapUserToRecordLabelDto(users.getContent());
        return recordLabelDtos;
    }


    public List<recordLabelDto> search(String searchParam) {
        List<users> users=userRepository.searchRecordLabels(searchParam);
        List<recordLabelDto> recordLabelDtos=mapUserToRecordLabelDto(users);
        return recordLabelDtos;
    }

    public List<recordLabelDto> mapUserToRecordLabelDto(List<users> users) {
        List<recordLabelDto> recordLabelDtos=users.stream().map(recordLabelUser->{
            recordLabelDto recordLabelDto=new recordLabelDto();
            recordLabelDto.setEmail(recordLabelUser.getEmail());
            recordLabelDto.setName(recordLabelUser.getName());
            recordLabel recordLabel=recordLabelServiceImpl.findByUserId(recordLabelUser.getId());
            recordLabelDto.setNumber(recordLabelArtistsServiceImpl.findNumberOfArtistsInRecordLabel(recordLabel.getId()));
            recordLabelDto.setId(recordLabel.getId());
            recordLabelDto.setAboutInfo(recordLabel.getAboutInfo());
            if(recordLabelUser.getPicture()!=null){
                recordLabelDto.setPictureUrl(amazonS3ClientService.getPictureUrl(recordLabelUser.getPicture()));
            }   else {
                recordLabelDto.setPictureUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getRecordLabelDefaultPicture()).getValue()));
            }
            return recordLabelDto;
        }).collect(Collectors.toList());
        return recordLabelDtos;
    }

    public List<artistDto> findArtists(int userId,Pageable pageable) {
        recordLabel recordLabel=recordLabelServiceImpl.findByUserId(userId);
        Page<artist> artists=artistServiceImpl.findAllArtists(recordLabel.getId(),pageable);
        List<artistDto> artistsDto=artistServiceImpl.mapArtistToArtistDto(artists.getContent());
        return artistsDto;
    }


    public List<recordLabelDto> findAndSortRecordLabels(String sortParam,int page,int size,int direction) {
        List<recordLabelDto> recordLabelDtos;
        Page<users> users;
        if(!sortParam.equals("number")) {
            if (direction == 0) {
                users = userRepository.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE, page*size+size, Sort.Direction.DESC, sortParam));
            } else {
                users = userRepository.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE, page*size+size, Sort.Direction.ASC, sortParam));
            }

            recordLabelDtos=mapUserToRecordLabelDto(users.getContent());

        }
        else {
                users = userRepository.findRecordLabels(PageRequest.of(RECORD_LABEL_LAZY_LOAD_INITIAL_PAGE, page*size+size));
                recordLabelDtos=mapUserToRecordLabelDto(users.getContent());

                if(direction==0) {
                    Collections.sort(recordLabelDtos,Collections.reverseOrder());
                }   else {
                    Collections.sort(recordLabelDtos);
                }


        }
        return recordLabelDtos;
    }


    public List<artistDto> findArtists(Pageable pageable) {
        recordLabel recordLabel= springChecks.getLoggedInRecordLabel();
        Page<users> artists;
        artists = userRepository.findArtists(recordLabel.getId(), pageable);
        List<artistDto> artistDtos=mapUserToArtistDto(artists.getContent());
        return artistDtos;
    }

    public List<artistDto> searchArtists(String searchParam) {
        recordLabel recordLabel= springChecks.getLoggedInRecordLabel();
        List<users> artists=userRepository.searchArtists(recordLabel.getId(),searchParam);
        List<artistDto> artistDtos=mapUserToArtistDto(artists);
        return artistDtos;
    }

    public List<artistDto> mapUserToArtistDto(List<users> artists) {
        List<artistDto> artistDtos=artists.stream().map(artist->{
            artistDto artistDto=new artistDto();
            artistDto.setName(artist.getName());
            artistDto.setEmail(artist.getEmail());
            artist thisArtist=artistServiceImpl.findByUserId(artist.getId());
            artistDto.setId(thisArtist.getId());
            artistDto.setDeleted(thisArtist.isDeleted());
            Optional<recordLabelArtists> recordLabelArtists=recordLabelArtistsServiceImpl.findByArtistId(thisArtist.getId());

            if(!thisArtist.isDeleted() && recordLabelArtists.isPresent()) {
                artistDto.setRecordLabelName(recordLabelArtists.get().getRecordLabel().getUser().getName());
                artistDto.setRecordLabelId(recordLabelArtists.get().getRecordLabel().getId());
            }
            if(artist.getPicture()!=null) {
                artistDto.setPictureUrl(amazonS3ClientService.getPictureUrl(artist.getPicture()));

            }   else {
                artistDto.setPictureUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getArtistDefaultPicture()).getValue()));
            }
            return artistDto;
        }).collect(Collectors.toList());

        return artistDtos;
    }



    public void setUserPassword(String token, String password) {
        users user=verificationTokenServiceImpl.findByToken(token);
        user.setActivated(true);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        saveUser(user);
        verificationTokenServiceImpl.delete(token);
    }


    public void setUserPassword(users user,String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        saveUser(user);
    }

    public Page<users> findAllRecentlyAddedArtists(Pageable pageable) {
        return userRepository.findAllRecentlyAddedArtists(pageable);
    }

    public Page<users> findAllActiveRecordLabels(Pageable pageable) {
        return userRepository.findAllActivatedRecordLabels(pageable);
    }

    public List<users>findAllActiveRecordLabels() {
        return userRepository.findAllActivatedRecordLabels();
    }

    public List<artistDto> searchAllExactMatchArtists(String param) {
        List<users> artistsExactMatch=userRepository.findAllExactMatchArtists(param);
        List<artistDto> artistsDtoExactMatch=mapUserToArtistDto(artistsExactMatch);
        return artistsDtoExactMatch;
    }

    public List<recordLabelDto> searchAllExactMatchRecords(String param) {

        List<users> recordsExactMatch=userRepository.findAllExactMatchRecords(param);
        List<recordLabelDto> records=mapUserToRecordLabelDto(recordsExactMatch);
        return records;
    }

    public List<artistDto> searchAllStartingSearchQuery(String param) {

        List<users> artistsExactMatch=userRepository.findAllStartingSearchQuery(param);
        List<artistDto> artistsDtoExactMatch=mapUserToArtistDto(artistsExactMatch);
        return artistsDtoExactMatch;
    }

    public List<recordLabelDto> searchAllRecordsStartingSearchQuery(String param) {
        List<users> recordsExactMatch=userRepository.findAllRecordsStartingSearchQuery(param);
        List<recordLabelDto> records=mapUserToRecordLabelDto(recordsExactMatch);
        return records;
    }

    public List<artistDto> searchAllArtists(String param) {
        List<users> artists=userRepository.findAllArtists(param);
        List<artistDto> artistsDtos=mapUserToArtistDto(artists);
        return artistsDtos;
    }

    public List<recordLabelDto> searchAllRecordsLabels(String param) {
        List<users> recordsLabels=userRepository.findAllRecordsLabels(param);
        List<recordLabelDto> records=mapUserToRecordLabelDto(recordsLabels);
        return records;
    }

}

