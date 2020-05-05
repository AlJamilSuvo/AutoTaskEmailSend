package al.jamil.suvo.autoemail.config;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigProvider {
    private ConfigProvider() throws FileNotFoundException {
        Gson gson = new Gson();
        config = gson.fromJson(new FileReader("Config.json"), Config.class);

    }

    private static ConfigProvider configProvider;
    public Config config;

    public static void init() throws FileNotFoundException {
        configProvider = new ConfigProvider();
    }

    public static ConfigProvider get(){
        return configProvider;
    }
}
