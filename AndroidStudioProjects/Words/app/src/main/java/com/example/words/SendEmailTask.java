package com.example.words;

import android.util.Log;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmailTask implements Runnable {
    private final static String TAG = "EmailSender";
    private static final String USERNAME = "gusdimus1@gmail.com";
    private static final String PASSWORD = "ergcuhgoxwyjeduw";
    private final String subject;
    private final String message;
    private final String recipient;

    public SendEmailTask(String subject, String message, String recipient) {
        this.subject = subject;
        this.message = message;
        this.recipient = recipient;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "Sending email to " + recipient);
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

            Message emailMessage = new MimeMessage(session);
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            Transport.send(emailMessage);

            Log.d(TAG, "Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            Log.e(TAG, "Failed to send email to " + recipient, e);
        }
    }
}