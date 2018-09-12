package com.app.MBox.aditional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class authenticatedUser extends User {

    private int userId;

    public authenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities, int userId) {
        super(username, password, true, true, true, true, authorities);
        this.userId=userId;
    }

    public int getUserId() {
        return userId;
    }

}
