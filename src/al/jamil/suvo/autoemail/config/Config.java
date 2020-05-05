package al.jamil.suvo.autoemail.config;

public class Config {
    String senderEmail;
    String senderName;
    String password;
    int sendingTimeHour;
    int sendingTimeMin;
    String receiverEmail;
    String ccEmail;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSendingTimeHour() {
        return sendingTimeHour;
    }

    public void setSendingTimeHour(int sendingTimeHour) {
        this.sendingTimeHour = sendingTimeHour;
    }

    public int getSendingTimeMin() {
        return sendingTimeMin;
    }

    public void setSendingTimeMin(int sendingTimeMin) {
        this.sendingTimeMin = sendingTimeMin;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getCcEmail() {
        return ccEmail;
    }

    public void setCcEmail(String ccEmail) {
        this.ccEmail = ccEmail;
    }
}
