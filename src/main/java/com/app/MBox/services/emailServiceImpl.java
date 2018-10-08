package com.app.MBox.services;

import com.app.MBox.common.enumeration.emailTemplateEnum;
import com.app.MBox.config.properties;
import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.core.model.emailTemplate;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.aboutMessageDto;
import com.app.MBox.dto.emailBodyDto;
import com.app.MBox.dto.sendEmailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service("emailService")
@Slf4j
public class emailServiceImpl implements emailService{

    @Autowired
    private properties properties;

    @Autowired
    private configurationService configurationServiceImpl;

    @Autowired
    private springChecks springChecks;

    @Autowired
    private emailTemplateService emailTemplateService;

    @Async
   private void sendMail(sendEmailDto sendEmail) {
        try {
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", configurationServiceImpl.findByKey(properties.getSmtpServerPort()).getValue());
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(configurationServiceImpl.findByKey(properties.getFromUserEmail()).getValue(), sendEmail.getFromUserFullName()));
                if(springChecks.isProductionProfile()) {
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(sendEmail.getToEmail()));
                }   else {
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(properties.getToEmailAdress()));
                }
            msg.setSubject(sendEmail.getSubject());
            msg.setContent(sendEmail.getBody(), "text/html");

            Transport transport = session.getTransport();
            transport.connect(configurationServiceImpl.findByKey(properties.getSmtpServerHost()).getValue(),configurationServiceImpl.findByKey(properties.getSmtpUserName()).getValue(),configurationServiceImpl.findByKey(properties.getSmtpUserPassword()).getValue());
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Async
    public void setAndSendEmail(emailBodyDto emailBodyDto, users user) {

        sendEmailDto sendEmail=new sendEmailDto();
        sendEmail.setBody(emailBodyDto.getBody());
        sendEmail.setSubject(emailBodyDto.getSubject());
        sendEmail.setFromUserFullName(user.getName());
        sendEmail.setToEmail(user.getEmail());
        sendMail(sendEmail);

    }
    @Async
    public void sendDeleteArtistEmail(users user) {
        emailTemplate emailTemplate= emailTemplateService.findByName(emailTemplateEnum.deleteArtistMail.toString());
        String body=emailTemplate.getBody().replace(properties.getNAME(),user.getName());
        emailBodyDto emailBodyDto=new emailBodyDto();
        emailBodyDto.setBody(body);
        emailBodyDto.setSubject(emailTemplate.getSubject());
        setAndSendEmail(emailBodyDto,user);
    }
    @Async
    public void sendDeleteRecordLabelEmail(users user) {
        emailTemplate emailTemplate= emailTemplateService.findByName(emailTemplateEnum.deleteRecordLabelMail.toString());
        String body=emailTemplate.getBody().replace(properties.getNAME(),user.getName());
        emailBodyDto emailBodyDto=new emailBodyDto();
        emailBodyDto.setBody(body);
        emailBodyDto.setSubject(emailTemplate.getSubject());
        setAndSendEmail(emailBodyDto,user);
    }

    public void sendAboutEmail(aboutMessageDto aboutMessageDto) {
        sendEmailDto sendEmailDto=new sendEmailDto();
        sendEmailDto.setBody(String.format("%s <br\\>Sent from %s",aboutMessageDto.getMessage(),aboutMessageDto.getEmail()));
        sendEmailDto.setSubject(properties.getAboutMailSubject());
        sendEmailDto.setToEmail(properties.getToEmailAdress());
        sendEmailDto.setFromUserFullName(aboutMessageDto.getName());
        sendMail(sendEmailDto);
    }
}
