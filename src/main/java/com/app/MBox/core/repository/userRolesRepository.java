package com.app.MBox.core.repository;

import com.app.MBox.core.model.userRoles;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface userRolesRepository extends CrudRepository <userRoles,Integer> {

    userRoles findByUserId(int userId);
    Set<userRoles> findByRoleId(int roleId);
}
