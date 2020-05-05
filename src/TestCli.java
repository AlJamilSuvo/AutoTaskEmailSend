import al.jamil.suvo.autoemail.config.Config;
import al.jamil.suvo.autoemail.emailio.EmailUtil;
import com.google.gson.Gson;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class TestCli {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        config.setSenderEmail("jamil.suvo@revesoft.com");
        config.setSenderName("Al-Jamil Suvo");
        config.setReceiverEmail("dhiman@revesoft.com");
        config.setCcEmail("maniruzzaman@revesoft.com");
        config.setPassword("hcbxrTDAGGW");
        config.setSendingTimeHour(17);
        config.setSendingTimeMin(30);
        Gson gson = new Gson();
        String str = gson.toJson(config);
        System.out.println(str);

        FileWriter fileWriter = new FileWriter("Config.json");
        fileWriter.write(str);
        fileWriter.flush();


    }

}
