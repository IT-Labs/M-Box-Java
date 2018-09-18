package com.app.MBox.aditional;

import com.app.MBox.core.model.recordLabel;
import com.app.MBox.core.model.users;
import com.app.MBox.services.recordLabelServiceImpl;
import com.app.MBox.services.userServiceImpl;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

@Component
@NoArgsConstructor
public class springChecks {

    @Autowired
    private Environment environment;
    @Autowired
    recordLabelServiceImpl recordLabelServiceImpl;
    @Autowired
    userServiceImpl userServiceImpl;

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

}