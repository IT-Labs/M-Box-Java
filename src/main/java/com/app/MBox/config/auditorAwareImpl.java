package com.app.MBox.config;

import com.app.MBox.common.customHandler.authenticatedUser;
import com.app.MBox.services.userService;
import com.app.MBox.services.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class auditorAwareImpl implements AuditorAware<Integer> {
@Autowired
userService userServiceImpl;
    @Override

    public Optional<Integer> getCurrentAuditor() {
        Object object= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(object instanceof authenticatedUser) {
            authenticatedUser authenticatedUser =(authenticatedUser)object;
            return Optional.of(authenticatedUser.getUserId());

        }   else {
            return Optional.of(-1);

        }






    }


}
