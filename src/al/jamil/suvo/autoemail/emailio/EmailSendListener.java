package al.jamil.suvo.autoemail.emailio;

public interface EmailSendListener {
    void updateSendingInfo(String info);

    void emailSendingResult(boolean isSuccessful);
}
