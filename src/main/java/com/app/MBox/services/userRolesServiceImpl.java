package com.app.MBox.services;

import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.core.model.userRoles;
import com.app.MBox.core.model.users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.MBox.core.repository.userRolesRepository;

import java.util.Set;

@Service("userRolesServiceImpl")
public class userRolesServiceImpl implements userRolesService {
    @Autowired
    private userRolesRepository userRolesRepository ;
    @Autowired
    verificationTokenService verificationTokenService;


    public userRoles findByUserId(Integer userId) {

        return userRolesRepository.findByUserId(userId);
    }

    public Set <userRoles> findByRoleId(Integer roleId) {

        return userRolesRepository.findByRoleId(roleId);
    }


    public void saveUserRoles(userRoles userRoles) {

        userRolesRepository.save(userRoles);
    }

    public void deleteUserRoles(userRoles userRoles) {
          userRolesRepository.delete(userRoles);
    }

    public String getUserRoleByToken(String token) {
        users user=verificationTokenService.findByToken(token);
        userRoles userRoles=findByUserId(user.getId());
        return getRole(userRoles);
    }


    public String getRole(userRoles userRoles) {
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
