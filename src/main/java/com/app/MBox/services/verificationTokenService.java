package com.app.MBox.services;

import com.app.MBox.core.model.users;
import com.app.MBox.core.model.verificationToken;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public interface verificationTokenService {

     verificationToken findByUserId(int id);

     users findByToken(String token);

     void saveVerificationToken(verificationToken verificationToken);

     verificationToken findByVerificationToken(String token) ;

     void delete(String token) ;

     boolean confirmUser(String token) ;

     boolean checkTokenExpired(String token) ;

     boolean checkIfExpiredDate(Date dateCreated) ;

     verificationToken createToken(users user) ;

}
