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
    userService userServiceImpl;
    @Autowired
    verificationTokenService verificationTokenServiceImpl;
    @Autowired
    artistService artistServiceImpl;
    @Autowired
    userRolesService userRolesServiceImpl;
    @Autowired
    userRolesService userRolesService;

    public boolean isProductionProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0 && profiles[0].equals("production")) {
            return true;

        }
        return false;
    }

    public recordLabel getLoggedInRecordLabel() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recordLabel recordLabel=recordLabelServiceImpl.findByUserId(authenticatedUser.getUserId());
        return recordLabel;
    }



    public String getLoggedInUserRole() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        users user=userServiceImpl.findByEmail(authenticatedUser.getUsername());
        userRoles userRoles=userRolesServiceImpl.findByUserId(user.getId());
        return userRolesService.getRole(userRoles);
    }



}