//package com.nics.e_malchin_service.Service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.mail.MailException;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.*;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//@Service
//public class MailService {
//
//    private final JavaMailSender mailSender;
//
//    @Autowired
//    public MailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    /**
//     * Send a simple text email.
//     */
//    public void sendSimpleMessage(String to, String subject, String text) throws MailException {
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(to);
//        msg.setSubject(subject);
//        msg.setText(text);
//        mailSender.send(msg);
//    }
//
//    /**
//     * Send an HTML email.
//     */
//    public void sendHtmlMessage(String to, String subject, String htmlBody)
//            throws MessagingException, MailException {
//        MimeMessage mime = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlBody, true); // true = isHtml
//        mailSender.send(mime);
//    }
//
//    /**
//     * Send an email with an attachment loaded from the classpath.
//     *
//     * @param to          recipient
//     * @param subject     subject
//     * @param text        body text (plain)
//     * @param resourcePath classpath location (e.g. "static/report.pdf")
//     * @param attachmentName name to show in the email
//     */
//    public void sendMessageWithAttachment(
//            String to,
//            String subject,
//            String text,
//            String resourcePath,
//            String attachmentName
//    ) throws MessagingException, MailException {
//        MimeMessage mime = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mime, true);
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(text, false);
//
//        Resource file = new ClassPathResource(resourcePath);
//        helper.addAttachment(attachmentName, file);
//
//        mailSender.send(mime);
//    }
//}
