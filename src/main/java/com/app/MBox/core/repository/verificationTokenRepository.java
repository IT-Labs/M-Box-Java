package com.app.MBox.core.repository;

import com.app.MBox.core.model.verificationToken;
import org.springframework.data.repository.CrudRepository;

public interface verificationTokenRepository extends CrudRepository <verificationToken,Integer> {

    verificationToken findByUserId(int userId);

    verificationToken findByToken(String token);

}
