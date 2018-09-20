package com.app.MBox.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties("app")
public class properties {

    private  String example;
    private  String passwordMessage;
    private  String smtpServerHost;
    private  String smtpServerPort;
    private  String smtpUserName;
    private  String smtpUserPassword;
    private  String fromUserEmail;
    private  String APPURL;
    private  String NAME;
    private  String EMAILADRESS;
    private  String PORT;
    private  String toEmailAdress;
    private  int artistLimit;
    private  String artistDefaultPicture;
    private String confirmUrl;
    private String resetPasswordUrl;
    private String joinUrl;
    private String songDefaultImage;
    private String passwordNotMatchMessage;
    private String incorrectEmailMessage;
    private String notAuthorizeMessage;
    private String invalidDataMessage;
    private String emailAlreadyExistsMessage;
    private String artistNumberMessage;
    private String csvExtensionError;
    private String incorectPasswordMessage;
}
