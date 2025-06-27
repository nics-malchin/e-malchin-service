//package com.nics.e_malchin_service.Controller;
//
//import com.nics.e_malchin_service.Service.MailReaderService;
//import com.nics.e_malchin_service.Service.MailService;
//import jakarta.mail.MessagingException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/mail")
//public class MailController {
//
//    private final MailService mailService;
//    private final MailReaderService readerService;
//
//    @Autowired
//    public MailController(MailService mailService, MailReaderService readerService) {
//        this.mailService = mailService;
//        this.readerService = readerService;
//    }
//
//    @PostMapping("/send")
//    public String send(@RequestParam String to,
//                       @RequestParam String subject,
//                       @RequestParam String body) {
//        mailService.sendSimpleMessage(to, subject, body);
//        return "Email sent to " + to;
//    }
//
//    @GetMapping("/unread")
//    public String fetchUnread() {
//        try {
//            readerService.fetchUnreadEmailsInfo();
//            return "Fetched unread email info (see logs).";
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            return "Failed to fetch: " + e.getMessage();
//        }
//    }
//}
