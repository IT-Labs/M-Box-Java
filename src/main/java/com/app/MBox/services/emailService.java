package com.app.MBox.services;

import com.app.MBox.aditional.properties;
import com.app.MBox.dto.sendEmailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("emailService")
@Getter
@Setter
@NoArgsConstructor
public class emailService {

    @Autowired
    private properties properties;

    @Autowired
    private configurationServiceImpl configurationServiceImpl;

    void sendMail(sendEmailDto sendEmail) {
        try {
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", configurationServiceImpl.findByKey(properties.getSmtpServerPort()).getValue());
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(configurationServiceImpl.findByKey(properties.getFromUserEmail()).getValue(), sendEmail.getFromUserFullName()));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(sendEmail.getToEmail()));
            msg.setSubject(sendEmail.getSubject());
            msg.setContent(sendEmail.getBody(), "text/html");

            Transport transport = session.getTransport();
            transport.connect(configurationServiceImpl.findByKey(properties.getSmtpServerHost()).getValue(),configurationServiceImpl.findByKey(properties.getSmtpUserName()).getValue(),configurationServiceImpl.findByKey(properties.getSmtpUserPassword()).getValue());
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
