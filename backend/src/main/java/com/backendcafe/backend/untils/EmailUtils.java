package com.backendcafe.backend.untils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailUtils {
    final private JavaMailSender emailSender;

    public EmailUtils(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
        log.info("to {} subject{} text {} list{}",to,subject,text,list);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("targtx980@hotmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (list != null && list.size() > 0)
            message.setCc(getCcArray(list));
        emailSender.send(message);
    }

    private String[] getCcArray(List<String> list) {
        String[] cc = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            cc[0] = list.get(i);
        }
        return cc;
    }

    public void sendResetPasswordEmail(String email, String resetUrl) {
                log.info("email {} url {}" ,email , resetUrl);
    }
}
