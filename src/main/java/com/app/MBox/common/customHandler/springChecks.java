package com.app.MBox.common.customHandler;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.recordLabel;
import com.app.MBox.core.model.userRoles;
import com.app.MBox.core.model.users;
import com.app.MBox.services.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class springChecks {

    @Autowired
    Environment environment;
    @Autowired
    recordLabelService recordLabelService;
    @Autowired
    userService userService;
    @Autowired
    userRolesService userRolesService;
    @Autowired
    artistService artistService;

    public boolean isProductionProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0 && profiles[0].equals("production")) {
            return true;
        }
        return false;
    }

    public recordLabel getLoggedInRecordLabel() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recordLabel recordLabel=recordLabelService.findByUserId(authenticatedUser.getUserId());
        return recordLabel;
    }

    public artist getLoggedInArtist() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        artist artist=artistService.findByUserId(authenticatedUser.getUserId());
        return artist;
    }


    public String getLoggedInUserRole() {
        try {
            authenticatedUser authenticatedUser = (authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            users user = userService.findByEmail(authenticatedUser.getUsername());
            userRoles userRoles = userRolesService.findByUserId(user.getId());
            return userRolesService.getRole(userRoles);
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }

    public int getLoggedInUserId() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedUser.getUserId();
    }

    public String getLoggedInUserEmail() {
        authenticatedUser authenticatedUser=(authenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedUser.getUsername();
    }

}