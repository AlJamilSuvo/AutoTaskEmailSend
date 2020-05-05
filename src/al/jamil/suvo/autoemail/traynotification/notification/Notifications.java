package al.jamil.suvo.autoemail.traynotification.notification;

import java.io.File;

public enum Notifications implements Notification {

    INFORMATION("img/info.png", "#2C54AB"),
    NOTICE("img/notice.png", "#8D9695"),
    APP("img/icon.png", "#009961"),
    SUCCESS("img/success.png", "#009961"),
    WARNING("img/warning.png", "#E23E0A"),
    SENDING("img/sending128.gif", "#E23E0A"),
    ERROR("img/error.png", "#CC0033");

    private final String urlResource;
    private final String paintHex;

    Notifications(String urlResource, String paintHex) {
        File file = new File(urlResource);
        this.urlResource = "file:///" + file.getAbsolutePath();
        this.paintHex = paintHex;
    }

    @Override
    public String getURLResource() {
        return urlResource;
    }

    @Override
    public String getPaintHex() {
        return paintHex;
    }

}
