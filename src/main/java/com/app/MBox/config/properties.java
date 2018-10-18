package com.app.MBox.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
public class properties {


    @Value("${ses.smtpServerHost}")
    private String smtpServerHost;

    @Value("${ses.smtpServerPort}")
    private String smtpServerPort;

    @Value("${ses.smtpUserName}")
    private String smtpUserName;

    @Value("${ses.smtpUserPassword}")
    private String smtpUserPassword;

    @Value("${ses.fromUserEmail}")
    private String fromUserEmail;

    @Value("${app.APPURL}")
    private  String APPURL;

    @Value("${app.NAME}")
    private  String NAME;

    @Value("${app.EMAILADRESS}")
    private  String EMAILADRESS;

    @Value("${app.toEmailAdress}")
    private  String toEmailAdress;

    @Value("${app.artistLimit}")
    private int artistLimit;

    @Value("${app.artistDefaultPicture}")
    private String artistDefaultPicture;

    @Value("${app.confirmUrl}")
    private String confirmUrl;

    @Value("${app.resetPasswordUrl}")
    private String resetPasswordUrl;

    @Value("${app.joinUrl}")
    private String joinUrl;

    @Value("${app.songDefaultPicture}")
    private String songDefaultPicture;

    @Value("${app.recordLabelDefaultPicture}")
    private String recordLabelDefaultPicture;

    @Value("${message.passwordMessage}")
    private  String passwordMessage;

    @Value("${message.passwordNotMatchMessage}")
    private String passwordNotMatchMessage;

    @Value("${message.incorrectEmailMessage}")
    private String incorrectEmailMessage;

    @Value("${message.notAuthorizeMessage}")
    private String notAuthorizeMessage;

    @Value("${message.invalidDataMessage}")
    private String invalidDataMessage;

    @Value("${message.emailAlreadyExistsMessage}")
    private String emailAlreadyExistsMessage;

    @Value("${message.artistNumberMessage}")
    private String artistNumberMessage;

    @Value("${message.csvExtensionError}")
    private String csvExtensionError;

    @Value("${message.incorectPasswordMessage}")
    private String incorectPasswordMessage;

    @Value("${message.imageExtensionError}")
    private String imageExtensionError;

    @Value("${message.maxUploadImageSize}")
    private String maxUploadImageSize;

    @Value("${message.aboutMailSubject}")
    private String aboutMailSubject;

    @Value("${message.successfullMessage}")
    private String successfullMessage;

    @Value("${message.expiredToken}")
    private String expiredToken;
}
