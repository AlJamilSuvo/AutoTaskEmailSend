package al.jamil.suvo.autoemail.fx;


import al.jamil.suvo.autoemail.controller.AutoEmailController;
import al.jamil.suvo.autoemail.traynotification.animations.Animations;
import al.jamil.suvo.autoemail.traynotification.notification.Notification;
import al.jamil.suvo.autoemail.traynotification.notification.Notifications;
import al.jamil.suvo.autoemail.traynotification.notification.TrayNotification;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class AutoEmailSender extends Application {
    AutoEmailController controller;
    boolean isDarkTheme = false;
    Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("auto_email_main_new.fxml"));
        Parent parent = fxmlLoader.load();
        controller = fxmlLoader.getController();
        scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Auto Task Email Send");
        File file = new File("img/icon.png");
        Image icon = new Image("file:///" + file.getAbsolutePath());
        primaryStage.getIcons().add(icon);
        createSystemTrayIcon(primaryStage);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            if (SystemTray.isSupported() && controller.showSendToSystemTray()) {
                hide(primaryStage);
            } else System.exit(0);
        });


    }



    private void createSystemTrayIcon(Stage stage) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                File file = new File("img/icon_tray.png");
                URL url = new URL("file:///" + file.getAbsolutePath());
                image = ImageIO.read(url);
            } catch (IOException ex) {
                ex.fillInStackTrace();
            }

            ActionListener showListener = e -> Platform.runLater(stage::show);
            ActionListener closeListener = e -> System.exit(0);
            ActionListener sendEmailNowListener = e -> {
                Platform.runLater(controller::doSendEmail);
            };
            ActionListener cancelAutoSendListener = e -> {
                Platform.runLater(controller::doCancelAutoSend);
            };

            PopupMenu popup = new PopupMenu();
            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);

            MenuItem sendEmailNow = new MenuItem("Send Email Now");
            sendEmailNow.addActionListener(sendEmailNowListener);
            popup.add(sendEmailNow);


            MenuItem cancelAutoSend = new MenuItem("Cancel Auto Send");
            cancelAutoSend.addActionListener(cancelAutoSendListener);
            popup.add(cancelAutoSend);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);
            TrayIcon trayIcon = new TrayIcon(image, "Auto Task Email Send", popup);
            trayIcon.addActionListener(showListener);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);

            }
        }


    }

    private void hide(final Stage stage) {
        Platform.runLater(() -> {
            if (SystemTray.isSupported()) {
                stage.hide();
                showProgramIsMinimizedMsg();
            } else {
                System.exit(0);
            }
        });
    }

    private void showProgramIsMinimizedMsg() {
        String title = "Still Running...";
        String message = "Auto task email Send is on system tray";
        Notification notification = Notifications.INFORMATION;

        TrayNotification tray = new TrayNotification();
        tray.setTitle(title);
        tray.setMessage(message);
        tray.setNotification(notification);
        tray.setAnimation(Animations.POPUP);
        tray.showAndDismiss(new Duration(10 * 1000));
    }
}
