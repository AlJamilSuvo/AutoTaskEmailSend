package al.jamil.suvo.autoemail.controller;

import al.jamil.suvo.autoemail.config.ConfigProvider;
import al.jamil.suvo.autoemail.emailio.EmailSendListener;
import al.jamil.suvo.autoemail.emailio.EmailSender;
import al.jamil.suvo.autoemail.fx.AutoEmailSender;
import al.jamil.suvo.autoemail.model.Task;
import al.jamil.suvo.autoemail.traynotification.animations.Animations;
import al.jamil.suvo.autoemail.traynotification.notification.Notification;
import al.jamil.suvo.autoemail.traynotification.notification.Notifications;
import al.jamil.suvo.autoemail.traynotification.notification.TrayNotification;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AutoEmailController {
    public TextField tfProject;
    public TextField tfTask;
    public TextField toEmail;
    public TextField ccEmail;
    public WebView emailView;
    public Label emailSendInfo;
    public TableView taskTable;
    public TextField tfSubject;
    public Button sendEmailButton;
    public ImageView sendingImage;
    public Text autoSendCancelBtn;
    public Text autoSendTime;
    public Label cancelEditingBt;
    public Button addTaskBtn;
    public ImageView ivAddTask;
    public ImageView ivSendBnt;
    private boolean isInEditMode = false;
    private Task editingTask = null;

    HashMap<String, Task> taskHashMap = new HashMap<>();
    List<Task> tasks = new ArrayList<>();

    Window window;
    Thread timeWatcher;
    boolean shouldTimeWatcherRun = true;
    Calendar sendingTime;
    Image imgAddTask;
    Image imgEditTask;

    public void setWindow(Window window) {
        this.window = window;
    }

    @FXML
    public void initialize() {


        try {
            ConfigProvider.init();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("No Config.json file");
            alert.showAndWait();
            System.exit(0);
            e.printStackTrace();
        }
        cancelEditingBt.setVisible(false);
        toEmail.setText(ConfigProvider.get().config.getReceiverEmail());
        ccEmail.setText(ConfigProvider.get().config.getCcEmail());
        TableColumn<String, Task> column1 = new TableColumn<>("Project");
        column1.setCellValueFactory(new PropertyValueFactory<>("project"));
        TableColumn<String, Task> column2 = new TableColumn<>("Task");
        column2.setCellValueFactory(new PropertyValueFactory<>("task"));
        taskTable.getColumns().addAll(column1, column2);
        TableColumn<Task, Void> column3 = new TableColumn<>("Delete");
        column3.setCellFactory(new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                final TableCell<Task, Void> cell = new TableCell<Task, Void>() {

                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction(event -> {
                            Task task = getTableView().getItems().get(getIndex());
                            tasks.remove(task);
                            taskTable.getItems().remove(task);
                            generateHTMLPreview();
                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });

        TableColumn<Task, Void> column4 = new TableColumn<>("Edit");
        column4.setCellFactory(new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                final TableCell<Task, Void> cell = new TableCell<Task, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction(event -> {
                            editingTask = getTableView().getItems().get(getIndex());
                            tfProject.setText(editingTask.getProject());
                            tfTask.setText(editingTask.getTask());
                            addTaskBtn.setText("Edit Task");
                            ivAddTask.setImage(imgEditTask);
                            cancelEditingBt.setVisible(true);

                            isInEditMode = true;
                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });


        taskTable.getColumns().addAll(column4, column3);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String day = simpleDateFormat.format(new Date());
        String subject = "Task List of " + day;
        tfSubject.setText(subject);

        sendingTime = Calendar.getInstance();
        sendingTime.set(Calendar.HOUR_OF_DAY, ConfigProvider.get().config.getSendingTimeHour());
        sendingTime.set(Calendar.MINUTE, ConfigProvider.get().config.getSendingTimeMin());
        sendingTime.set(Calendar.SECOND, 0);
        sendingTime.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aa");

        autoSendTime.setText(simpleDateFormat1.format(sendingTime.getTime()));


        timeWatcher = new Thread(() -> {
            while (shouldTimeWatcherRun) {
                Date date = new Date();
                long timeDiff = sendingTime.getTimeInMillis() - date.getTime();
                if (timeDiff > 0) {
                    Platform.runLater(() -> {

                        long differenceInSec = timeDiff / 1000;
                        if (differenceInSec == 5 * 60) {
                            showTimeWarning("5 Minutes");
                        } else if (differenceInSec == 30 * 60) {
                            showTimeWarning("30 Minutes");
                        } else if (differenceInSec == 60 * 60) {
                            showTimeWarning("1 Hour");
                        } else if (differenceInSec == 2 * 60 * 60) {
                            showTimeWarning("2 Hours");
                        }
                        long diffSeconds = timeDiff / 1000 % 60;
                        long diffMinutes = timeDiff / (60 * 1000) % 60;
                        long diffHours = timeDiff / (60 * 60 * 1000);

                        String remainingTime = String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
                        emailSendInfo.setText("Email will be sent within : " + remainingTime);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Platform.runLater(this::doSendEmail);
                    break;
                }
            }


        });
        Date date = new Date();
        long timeDiff = sendingTime.getTimeInMillis() - date.getTime();
        if (timeDiff > 0) timeWatcher.start();
        else {
            emailSendInfo.setText("Auto Send Time Over. Send Manually.");
        }
        generateHTMLPreview();

        File file1 = new File("img/add.png");
        File file2 = new File("img/edit.png");
        File file3 = new File("img/send_bnt.png");
        imgAddTask = new Image("file:///" + file1.getAbsolutePath());
        imgEditTask = new Image("file:///" + file2.getAbsolutePath());
        ivSendBnt.setImage(new Image("file:///" + file3.getAbsolutePath()));
        ivAddTask.setImage(imgAddTask);
        System.out.println(file1.getAbsolutePath());


    }


    private void showTimeWarning(String time) {
        Platform.runLater(() -> {
            String title = "In " + time + " Email will be Sent";
            String message = tasks.size() + " tasks in current list";
            Notification notification = Notifications.WARNING;

            TrayNotification tray = new TrayNotification();
            tray.setTitle(title);
            tray.setMessage(message);
            tray.setNotification(notification);
            tray.setAnimation(Animations.POPUP);
            tray.showAndDismiss(new Duration(1000 * 30));
        });
    }

    public void addTask(ActionEvent actionEvent) {
        if (isInEditMode) {
            editingTask.setProject(tfProject.getText());
            editingTask.setTask(tfTask.getText());
            isInEditMode = false;
            cancelEditingBt.setVisible(false);
            addTaskBtn.setText("Add New Task");
            ivAddTask.setImage(imgAddTask);
            editingTask = null;
        } else {
            Task task = new Task(tfProject.getText(), tfTask.getText(), System.currentTimeMillis());
            taskHashMap.put(task.toString(), task);
            taskTable.getItems().addAll(task);
            tasks.add(task);

        }
        tfTask.setText("");
        generateHTMLPreview();


        cancelEditingBt.setOnMouseClicked(event -> {
            cancelEditing(null);
        });
        autoSendCancelBtn.setOnMouseClicked(event -> {
            cancelAutoSent(null);
        });


    }

    private void generateHTMLPreview() {
        String html = getHTMLView();
        emailView.getEngine().loadContent(html);
    }

    private String getHTMLView() {
        String html = "<html><head>" +
                "<style>" +
                "table {" +
                "  border-collapse: collapse;" +
                "}" +
                "table, th, td {" +
                "  border: 1px solid black;" +
                "}" +
                "th, td {" +
                "  padding: 15px;" +
                "  text-align: left;" +
                "}" +
                "tr:nth-child(even) {background-color: #f2f2f2;}" +
                "</style>" +
                "</head><body>";

        html += "<h1>Task List</h1>";
        html += "<br>";
        html += "<table><tr>";

        String[] header = {"Index", "Project", "Task"};
        if (tasks.size() > 0) for (String h : header) html += "<th>" + h + "</th>";
        html += "</tr>";
        for (int i = 0; i < tasks.size(); i++) {
            html += "<tr><td>" + (i + 1) + "</td><td>" + tasks.get(i).getProject() + "</td><td>" + tasks.get(i).getTask() + "</td></tr>";
        }
        html += "</table>";
        html += "<br>";
        html += "---<br>";
        html += ConfigProvider.get().config.getSenderName();
        if (ConfigProvider.get().config.getSenderEmail().equals("jamil.suvo@revesoft.com")) {
            html += "<br><br><br><br>";
            html += "<p style=\"font-size=8px;color:gray;\">This email is auto generated and auto sent. This automation is done by " +
                    "this project. Github : <a href=\"https://github.com/AlJamilSuvo/AutoTaskEmailSend\">" +
                    "https://github.com/AlJamilSuvo/AutoTaskEmailSend</a></p>";


        }
        html += "</body></html>";
        System.out.println(html);
        return html;
    }


    boolean currentlySendingEmail = false;


    public void doSendEmail() {
        currentlySendingEmail = true;
        shouldTimeWatcherRun = false;
        sendEmailButton.setDisable(true);
        sendingImage.setVisible(true);
        File file = new File("img/sending64.gif");
        System.out.println(file.getAbsolutePath());


        String title = "Sending Email...";
        String message = "Email is generated with " + tasks.size() + " tasks";
        Notification notification = Notifications.SENDING;

        TrayNotification sendingTray = new TrayNotification();
        sendingTray.setTitle(title);
        sendingTray.setMessage(message);
        sendingTray.setAnimation(Animations.FADE);
        sendingTray.setNotification(notification);
        sendingTray.showAndWait();


        Image image = new Image("file:///" + file.getAbsolutePath());
        sendingImage.setImage(image);
        Runnable runnable = () -> EmailSender.sendEmail(tfSubject.getText(), getHTMLView(), toEmail.getText(), ccEmail.getText(), new EmailSendListener() {
            @Override
            public void updateSendingInfo(String info) {
                Platform.runLater(() -> {
                    emailSendInfo.setText(info);
                });
            }

            @Override
            public void emailSendingResult(boolean isSuccessful) {
                currentlySendingEmail = false;
                Platform.runLater(() -> {
                    sendingTray.dismiss();
                    sendEmailButton.setDisable(false);
                    if (isSuccessful) {
                        File file = new File("img/sent.png");
                        Image image = new Image("file:///" + file.getAbsolutePath());
                        sendingImage.setImage(image);

                        String title = "Email Sent";
                        String message = "Task email sent successfully";
                        Notification notification = Notifications.SUCCESS;

                        TrayNotification tray = new TrayNotification();
                        tray.setTitle(title);
                        tray.setMessage(message);
                        tray.setNotification(notification);
                        tray.showAndWait();


                    } else {
                        File file = new File("img/failed.png");
                        Image image = new Image("file:///" + file.getAbsolutePath());
                        sendingImage.setImage(image);

                        String title = "Failed";
                        String message = "Task email sent is failed";
                        Notification notification = Notifications.ERROR;

                        TrayNotification tray = new TrayNotification();
                        tray.setTitle(title);
                        tray.setMessage(message);
                        tray.setNotification(notification);
                        tray.showAndWait();
                    }
                });

            }
        });
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void sendEmail(ActionEvent actionEvent) {
        doSendEmail();
    }

    public void cancelAutoSent(ActionEvent actionEvent) {
        doCancelAutoSend();
    }

    public void doCancelAutoSend() {
        shouldTimeWatcherRun = false;
        emailSendInfo.setText("Auto Send canceled. Send manually.");
        autoSendCancelBtn.setDisable(true);
    }

    public void cancelEditing(ActionEvent actionEvent) {
        isInEditMode = false;
        cancelEditingBt.setVisible(false);
        addTaskBtn.setText("Add Task");
        ivAddTask.setImage(imgAddTask);
        editingTask = null;
        tfTask.setText("");

    }

    public boolean showSendToSystemTray() {
        Date date = new Date();
        long timeDiff = sendingTime.getTimeInMillis() - date.getTime();
        return currentlySendingEmail || timeDiff > 0;
    }


}
