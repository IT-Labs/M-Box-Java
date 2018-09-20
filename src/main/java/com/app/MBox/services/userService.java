package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.core.model.role;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.emailBodyDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.dto.userDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Component
public interface userService {

     users findByEmail(String email);

     users saveUser(users user);

     void delete (users user);



     users registerNewUserAccount(userDto accountDto, HttpServletRequest request) throws emailAlreadyExistsException;

     users createUser(userDto accountDto,role role) ;



     void savePassword(String password, String token) ;


     boolean forgotPassword(String email,HttpServletRequest request) ;



     emailBodyDto parsingEmailBody(users user, String appUrl, String templateName) ;

     List<recordLabelDto> findRecordLabels(Pageable pageable) ;


     List<recordLabelDto> search(String searchParam) ;

     List<artistDto> findArtists(int userId, Pageable pageable) ;


     List<recordLabelDto> findAndSortRecordLabels(String sortParam,int page,int size,int direction) ;


     List<artistDto> findAndSortArtists(Pageable pageable) ;

     List<artistDto> searchArtists(String searchParam) ;

     void setUserPassword(String token, String password) ;


}
