//package com.nics.e_malchin_service.Service;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;      // <-- from google-api-client-jackson2
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.GmailScopes;
//import com.google.api.services.gmail.model.Message;              // <-- Gmail API Message
//import jakarta.mail.MessagingException;                         // <-- from jakarta.mail-api
//import jakarta.mail.Session;                                     // <-- from jakarta.mail-api
//import jakarta.mail.internet.InternetAddress;                   // <-- from jakarta.mail-api
//import jakarta.mail.internet.MimeMessage;                       // <-- from jakarta.mail-api
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.nio.file.*;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//import java.util.List;
//
//import static java.util.Base64.getUrlEncoder;
//
//@Service
//public class GmailApiService {
//    private static final String APPLICATION_NAME = "e-malchin-service";
////    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
//
//    private final Gmail gmail;
//
//    public GmailApiService() throws GeneralSecurityException, IOException {
//        // 1) Load credentials.json from classpath
//        InputStream in = getClass().getClassLoader().getResourceAsStream("credentials.json");
//        if (in == null) throw new FileNotFoundException("credentials.json not found on classpath");
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // 2) Build OAuth flow
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JSON_FACTORY,
//                clientSecrets,
//                SCOPES
//        )
//                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
//                .setAccessType("offline")
//                .build();
//
//        // 3) Authorize
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//
//        // 4) Build Gmail client
//        gmail = new Gmail.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JSON_FACTORY,
//                credential
//        )
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    /** Build a MimeMessage using Jakarta Mail */
//    private MimeMessage createEmail(String to, String from, String subject, String bodyText)
//            throws MessagingException {
//        Session session = Session.getDefaultInstance(new java.util.Properties(), null);
//        MimeMessage email = new MimeMessage(session);
//        email.setFrom(new InternetAddress(from));
//        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
//        email.setSubject(subject);
//        email.setText(bodyText);
//        return email;
//    }
//
//    /** Convert MimeMessage to Gmail API Message */
//    private Message toGmailMessage(MimeMessage email) throws IOException, MessagingException {
//        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
//            email.writeTo(buffer);
//            String raw = getUrlEncoder().encodeToString(buffer.toByteArray());
//            Message msg = new Message();
//            msg.setRaw(raw);
//            return msg;
//        }
//    }
//
//    /** Send via the Gmail REST API */
//    public void sendEmail(String to, String subject, String body)
//            throws MessagingException, IOException {
//        MimeMessage email = createEmail(to, "me", subject, body);
//        Message message = toGmailMessage(email);
//        gmail.users().messages().send("me", message).execute();
//    }
//}
