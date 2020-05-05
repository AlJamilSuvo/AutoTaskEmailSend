package al.jamil.suvo.autoemail.emailio;

import al.jamil.suvo.autoemail.config.ConfigProvider;

import java.util.Date;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailUtil {


    public static void sendEmail(Session session, String receiver, String ccReceiver, String body, String subject, EmailSendListener emailSendListener) {
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(ConfigProvider.get().config.getSenderEmail(), ConfigProvider.get().config.getSenderName()));
            msg.setReplyTo(InternetAddress.parse(ConfigProvider.get().config.getSenderEmail(), false));
            msg.setSubject(subject);
            msg.setContent(body, "text/html");
            msg.setSentDate(new Date());
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver, false));
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccReceiver, false));
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ConfigProvider.get().config.getSenderEmail(), false));
            emailSendListener.updateSendingInfo("Sending email...");
            Transport.send(msg);
            emailSendListener.updateSendingInfo("Email sent!");
            emailSendListener.emailSendingResult(true);
        } catch (Exception e) {
            e.printStackTrace();
            emailSendListener.updateSendingInfo("Email sent failed");
            emailSendListener.emailSendingResult(false);

        }
    }
}
