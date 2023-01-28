package backend.email;

public interface EmailService {
    void sendSimpleMail(String sendTo, String token);

    String sendMailWithAttachment(String sendTo, String token);
}