package com.app.MBox.services;

import com.app.MBox.core.model.users;
import com.app.MBox.dto.aboutMessageDto;
import com.app.MBox.dto.emailBodyDto;

public interface emailService {


    void setAndSendEmail(emailBodyDto emailBodyDto, users user);
    void sendDeleteArtistEmail(users user) ;
    void sendDeleteRecordLabelEmail(users user);
    void sendAboutEmail(aboutMessageDto aboutMessageDto);
}
