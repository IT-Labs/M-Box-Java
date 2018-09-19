package com.app.MBox.services;

import com.app.MBox.core.model.configuration;
import org.springframework.stereotype.Component;

@Component
public interface configurationService {

    configuration findByKey(String key);
}
