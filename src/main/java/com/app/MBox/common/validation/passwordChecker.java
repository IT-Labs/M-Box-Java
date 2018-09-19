package com.app.MBox.common.validation;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@NoArgsConstructor
public class passwordChecker {

    public boolean isInvalidPassword(String password) {

        if(password.length()<8 || password.length()>64) {
            return true;
        }

        if (!Pattern.compile("[a-zA-Z]").matcher(password).find()) {
            return true;
        }

        if(!Pattern.compile( "[0-9]" ).matcher( password ).find() && !Pattern.compile("[$&+,:;=?@#|'<>.-^*()%!]").matcher(password).find() ) {
            return true;
        }

        return false;

    }

    public boolean doPasswordMatches(String password,String confirmPassword) {

        return password.equals(confirmPassword);

    }
}
