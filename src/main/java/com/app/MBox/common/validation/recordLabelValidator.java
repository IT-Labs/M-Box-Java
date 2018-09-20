package com.app.MBox.common.validation;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class recordLabelValidator {

public boolean isValid(String name,String email) {

    if(name.length()<2 || email.length()>50) {
        return false;
    }

    if(email.length()>320) {
        return false;
    }

    if(!EmailValidator.getInstance().isValid(email)) {
        return false;
    }

    return true;
}

}
