package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.core.model.role;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Component
public interface userService {

     users findByEmail(String email);

     users saveUser(users user);

     void delete (users user);



     users registerNewUserAccount(userDto accountDto) throws emailAlreadyExistsException;

     users createUser(userDto accountDto,role role) ;



     void savePassword(String password, String token) ;


     boolean forgotPassword(String email,HttpServletRequest request) ;



     emailBodyDto parsingEmailBody(users user, String appUrl, String templateName) ;

     List<recordLabelDto> findRecordLabels(Pageable pageable) ;


     List<recordLabelDto> search(String searchParam) ;

     List<artistDto> findArtists(int userId, Pageable pageable) ;


     List<recordLabelDto> findAndSortRecordLabels(String sortParam,int page,int size,int direction) ;


     List<artistDto> findArtists(Pageable pageable) ;

     List<artistDto> searchArtists(String searchParam) ;

     void setUserPassword(String token, String password) ;

     void setUserPassword(users user,String password);

     Page<users> findAllRecentlyAddedArtists(Pageable pageable);

     Page<users> findAllActiveRecordLabels(Pageable pageable);

     List<artistDto> mapUserToArtistDto(List<users> artists);

     List<users> findAllActiveRecordLabels();

     List<recordLabelDto> mapUserToRecordLabelDto(List<users> users);

     List<artistDto> searchAllExactMatchArtists(String param);

     List<recordLabelDto> searchAllExactMatchRecords(String param);

    List<artistDto> searchAllStartingSearchQuery(String param);

     List<recordLabelDto> searchAllRecordsStartingSearchQuery(String param);

     List<artistDto> searchAllArtists(String param);

     List<recordLabelDto> searchAllRecordsLabels(String param);

    listenerDto getListener();

     void saveListener(listenerDto listener);

     String addListenerPicture(MultipartFile file, int id);
}
