package com.app.MBox.services;

import com.app.MBox.core.model.role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.MBox.core.repository.roleRepository;

@Service("roleServiceImpl")
public class roleServiceImpl implements roleService {

    @Autowired
    private roleRepository roleRepository ;


    public role findByName(String name) {

        return roleRepository.findByName(name);
    }


    public void saveRole(role role) {

        roleRepository.save(role);
    }

    public role findById(int id) {
        return roleRepository.findById(id);
    }
}
