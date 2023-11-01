package com.groupproject.libraryManagementSystem.util;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail (String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setTo(toEmail);
        message.setText(body);
        mailSender.send(message);
    }
}
