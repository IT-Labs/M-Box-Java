package com.app.MBox.common.validation;

import com.app.MBox.dto.userDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class passwordMatchesValidator implements ConstraintValidator<passwordMatches, Object> {

    @Override
    public void initialize(passwordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        userDto user = (userDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}


