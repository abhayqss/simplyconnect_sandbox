package com.scnsoft.eldermark.monitoring.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${mail.replyTo}")
    private String replyTo;

    private final JavaMailSender mailSender;

    private final String subject;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender,  @Value("${spring.profiles.active}") String springProfile) {
        this.mailSender = mailSender;
        this.subject = "New monitoring events: " + springProfile;
    }

    @Override
    public void send(String email, String message) {
        logger.info("Sending email to {}", email);
        var msg = new SimpleMailMessage();
        msg.setFrom(replyTo);
        msg.setTo(email);
        msg.setSubject(subject);
        msg.setText(message);
        mailSender.send(msg);
    }
}
