package nlu.fit.web.souvenirecommerce.common.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static String username;
    private static String password;

    static {
        Properties props = ApplicationLoader.getProperties();
        username = getProperty(props, "mail.username", "app_mail");
        password = getProperty(props, "mail.password", "app_password");
    }

    private static Session getSession() throws MessagingException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new MessagingException("SMTP credentials are not configured. Please set mail.username/mail.password or app_mail/app_password.");
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static void send(String to, String subject, String content, String type) throws MessagingException {
        Message message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(content, type);
        Transport.send(message);
    }

    private static String getProperty(Properties props, String primaryKey, String fallbackKey) {
        String value = props.getProperty(primaryKey);
        if (value == null || value.isBlank()) {
            value = props.getProperty(fallbackKey);
        }
        return value;
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
