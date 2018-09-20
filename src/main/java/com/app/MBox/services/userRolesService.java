package com.app.MBox.services;

import com.app.MBox.core.model.userRoles;
import org.springframework.stereotype.Component;

import java.util.Set;

public interface userRolesService {

     userRoles findByUserId(Integer userId);

     Set<userRoles> findByRoleId(Integer roleId);

     void saveUserRoles(userRoles userRoles);

     void deleteUserRoles(userRoles userRoles);

     public String getUserRoleByToken(String token) ;


     public String getRole(userRoles userRoles) ;

}
