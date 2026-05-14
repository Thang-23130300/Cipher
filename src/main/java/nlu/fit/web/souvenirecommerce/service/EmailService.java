package nlu.fit.web.souvenirecommerce.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void send(String to, String subject, String content, String type) throws MessagingException;
}
