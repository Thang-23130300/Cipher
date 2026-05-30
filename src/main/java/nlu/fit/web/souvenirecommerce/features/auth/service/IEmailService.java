package nlu.fit.web.souvenirecommerce.features.auth.service;

import jakarta.mail.MessagingException;

public interface IEmailService {

    void send(String to, String subject, String content, String type) throws MessagingException;
}
