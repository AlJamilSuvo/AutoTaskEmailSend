package al.jamil.suvo.autoemail.emailio;

import al.jamil.suvo.autoemail.config.Config;
import al.jamil.suvo.autoemail.config.ConfigProvider;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String subject, String body, String receiver, String ccReceiver, EmailSendListener listener) {
        String fromMail = ConfigProvider.get().config.getSenderEmail();
        String password = ConfigProvider.get().config.getPassword();
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.revesoft.com"); //SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromMail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        listener.updateSendingInfo("Session created. Auth Successful");
        EmailUtil.sendEmail(session, receiver, ccReceiver, body, subject, listener);
    }
}
