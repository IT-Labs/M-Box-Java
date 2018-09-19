package com.app.MBox.services;

import com.app.MBox.core.model.role;
import org.springframework.stereotype.Component;

@Component
public interface roleService {


     role findByName(String name);

     void saveRole(role role);

     role findById(int id);
}
