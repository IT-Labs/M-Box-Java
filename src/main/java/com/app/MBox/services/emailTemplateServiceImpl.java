package com.app.MBox.services;

import com.app.MBox.core.model.emailTemplate;
import com.app.MBox.core.repository.emailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("emailTemplateServiceImpl")
public class emailTemplateServiceImpl implements emailTemplateService {

    @Autowired
    private emailTemplateRepository emailTemplateRepository ;


    public emailTemplate findByName(String name) {

        return emailTemplateRepository.findByName(name);
    }


    public void saveEmailTemplate(emailTemplate emailTemplate) {

        emailTemplateRepository.save(emailTemplate);
    }

}
