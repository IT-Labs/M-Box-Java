package com.app.MBox.services;

import com.app.MBox.core.model.emailTemplate;
import org.springframework.stereotype.Component;

@Component
public interface emailTemplateService {

     emailTemplate findByName(String name) ;


     void saveEmailTemplate(emailTemplate emailTemplate) ;
}
