package com.app.MBox.services;


import com.app.MBox.aditional.authenticatedUser;
import com.app.MBox.core.model.users;
import com.app.MBox.core.model.userRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class userDetailsServiceImpl implements UserDetailsService {

    @Autowired
    userService userService;

    @Autowired
    roleService roleService;

    @Autowired
    userRolesService userRolesService;

    @Override
    public authenticatedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        users activeUser=userService.findByEmail(email);
            if (activeUser!=null && activeUser.isActivated()) {
                userRoles userRoles=userRolesService.findByUserId(activeUser.getId());

                if(userRoles!=null) {
                    GrantedAuthority authority = new SimpleGrantedAuthority(userRoles.getRole().getName());
                    authenticatedUser authenticatedUser = new authenticatedUser(activeUser.getEmail(), activeUser.getPassword(), Arrays.asList(authority),activeUser.getId());
                    return authenticatedUser;
                }
            }

            return null;
    }
}
