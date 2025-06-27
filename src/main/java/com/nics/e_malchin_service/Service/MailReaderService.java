//package com.nics.e_malchin_service.Service;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.search.FlagTerm;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.Properties;
//
//@Service
//public class MailReaderService {
//
//    @Value("${mail.imap.host}")      private String imapHost;
//    @Value("${mail.imap.port}")      private String imapPort;
//    @Value("${mail.imap.user}")      private String username;
//    @Value("${mail.imap.password}")  private String password;
//    @Value("${mail.imap.ssl.enable}") private String sslEnable;
//    @Value("${mail.imap.auth}")      private String imapAuth;
//
//    /**
//     * Connects via IMAP and prints basic info about unread messages.
//     */
//    public void fetchUnreadEmailsInfo() throws MessagingException {
//        Properties props = new Properties();
//        props.put("mail.store.protocol", "imap");
//        props.put("mail.imap.host", imapHost);
//        props.put("mail.imap.port", imapPort);
//        props.put("mail.imap.ssl.enable", sslEnable);
//        props.put("mail.imap.auth", imapAuth);
//
//        Session session = Session.getInstance(props);
//        try (Store store = session.getStore("imap")) {
//            store.connect(username, password);
//
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_ONLY);
//
//            // Search for unseen messages
//            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
//            System.out.println("You have " + messages.length + " unread messages");
//
//            for (Message msg : messages) {
//                Address[] froms = msg.getFrom();
//                String sender = (froms == null) ? "unknown" : ((InternetAddress) froms[0]).getAddress();
//                System.out.printf("• Subject: %s%n  From: %s%n  Received: %s%n%n",
//                        msg.getSubject(), sender, msg.getReceivedDate());
//            }
//
//            inbox.close(false);
//        }
//    }
//}
