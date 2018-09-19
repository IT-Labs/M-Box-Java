package com.app.MBox.common.customHandler;

import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.recordLabel;
import com.app.MBox.core.model.userRoles;
import com.app.MBox.core.model.users;
import com.app.MBox.services.*;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class springChecks {

    @Autowired
    private Environment environment;
    @Autowired
    recordLabelService recordLabelServiceImpl;
    @Autowired
    userServiceImpl userServiceImpl;
    @Autowired
    verificationTokenService verificationTokenServiceImpl;
    @Autowired
    artistService artistServiceImpl;
    @Autowired
    userRolesService userRolesServiceImpl;

    public boolean isProductionProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0 && profiles[0].equals("production")) {
            return true;

        }
        return false;
    }

    public recordLabel getAuthenticatedUser() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recordLabel recordLabel=recordLabelServiceImpl.findByUserId(authenticatedUser.getUserId());
        return recordLabel;
    }

    public String getUserByToken(String token) {
        users user=verificationTokenServiceImpl.findByToken(token);
        userRoles userRoles=userRolesServiceImpl.findByUserId(user.getId());
        if(userRoles.getRole().getName().equals(rolesEnum.ARTIST.name())) {
            return rolesEnum.ARTIST.name();
        }

        if(userRoles.getRole().getName().equals(rolesEnum.RECORDLABEL.name())) {
            return rolesEnum.RECORDLABEL.name();
        }

        if(userRoles.getRole().getName().equals(rolesEnum.ADMIN.name())) {
            return rolesEnum.ADMIN.name();
        }

        if(userRoles.getRole().getName().equals(rolesEnum.LISTENER.name())) {
            return rolesEnum.LISTENER.name();
        }

        return null;
    }

}