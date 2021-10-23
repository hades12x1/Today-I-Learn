package com.redamessoudi.emailsreceiver.configuration;

import com.sun.mail.imap.IMAPFolder;
import org.jsoup.Jsoup;

import java.util.Properties;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

/**
 * SMTP: Dùng để gửi mail. PORT: 25, 465(SSL), 587(StartTLS)
 * IMAP: Dùng để nhận email. PORT: 143, 993(SSL), Máy chạm chỉ đồng bộ h => Server tốn tài nguyên
 * POP3: Dùng để nhận email. PORT: 110, 995(SSL), Máy chạm download toàn bộ email trên server(có thể để lại 1 bản).
 */
public class EmailReceiver {

    public void downloadEmails(String host, String port, String userName, String password) {
 
        try {
            // connects to the message store
            Message[] messages = this.fetchMessages(host, Integer.valueOf(port), userName, password, false);

            Thread.sleep(100000000);
            for (int i = 0; i < messages.length; i++) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg.getRecipients(RecipientType.TO));
                String ccList = parseAddresses(msg.getRecipients(RecipientType.CC));
                String sentDate = msg.getSentDate() != null ? msg.getSentDate().toString() : "";
 
                String messageContent = this.getTextFromMessage(msg);
 
                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t To: " + toList);
                System.out.println("\t CC: " + ccList);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")){
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            String result = "";
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i ++){
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")){
                    result = result + "\n" + bodyPart.getContent();
                    break;  //without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")){
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();
                }
            }
            return result;
        }
        return "";
    }

    private String parseAddresses(Address[] address) {
        String listAddress = "";
 
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
 
        return listAddress;
    }

    public Message[] fetchMessages(String host, Integer port, String user, String password, boolean read) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore();
        store.connect(host,port, user, password);

        IMAPFolder emailFolder = (IMAPFolder) store.getFolder("INBOX");

        // Chỉ có imap mới add listener đc, giao thức POP3 thì không thể.
        emailFolder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                Message[] messages = messageCountEvent.getMessages();
                for (Message message : messages) {
                    try {
                        System.out.println(message.getSubject());
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) {
                messageCountEvent.getMessages();
                System.out.println("okeeeee");
            }
        });
        // use READ_ONLY if you don't wish the messages
        // to be marked as read after retrieving its content
        if (!emailFolder.isOpen())
            emailFolder.open(Folder.READ_WRITE);
        emailFolder.idle();

//        Code bên dưới get đoạn email chưa đọc tuỳ theo giá trị của biến read.
//        // search for all "unseen" messages
//        Flags seen = new Flags(Flags.Flag.SEEN);
//        FlagTerm unseenFlagTerm = new FlagTerm(seen, read);
//        return emailFolder.search(unseenFlagTerm);
        return null;
    }


    public static void main(String[] args) {
        String host = "proxx.emailserver.vn";
        String port = "993";  // Port giao thức
 
        String userName = "chuyenns";
        String password = "password";
 
        EmailReceiver receiver = new EmailReceiver();
        receiver.downloadEmails(host, port, userName, password);
    }
}