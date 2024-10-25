package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.SendMailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SendMailServiceImpl extends BaseService implements SendMailService {
    JavaMailSender mvJavaMailSender;

    @Override
    public boolean sendMail(String subject, String to, String body) throws UnsupportedEncodingException, MessagingException {
        Assert.notNull(subject, "Subject cannot be null!");
        Assert.notNull(to, "Recipient cannot be null!");
        Assert.notNull(body, "Content cannot be null!");

        MimeMessage mimeMessage = mvJavaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setSubject(subject);
        messageHelper.setTo(to);
        messageHelper.setText(body, true);

        mvJavaMailSender.send(mimeMessage);

        return true;
    }
}