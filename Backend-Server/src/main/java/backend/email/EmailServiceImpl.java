package backend.email;


import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final String msgBody = "Your registration is: http://localhost:8080/user/verifyRegistration?token=";
    private final String subject = "Verification Token";
    private final JavaMailSender javaMailSender;

    public void sendSimpleMail(String sendTo, String token) {
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom("escapingthetrenchlol@gmail.com");
            mailMessage.setTo(sendTo);
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);

            // Sending the mail
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.out.println("Could not send Email because of: " + e);
        }
    }

    public String sendMailWithAttachment(String sendTo, String token) {
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("escapingthetrenchlol@gmail.com");
            mimeMessageHelper.setTo(sendTo);
            mimeMessageHelper.setText(msgBody);
            mimeMessageHelper.setSubject(subject);

            //To be implemented later if needed mail + attachment
           /* FileSystemResource file = new FileSystemResource(
                    new File();

            mimeMessageHelper.addAttachment(
                    Objects.requireNonNull(file.getFilename()), file);*/

            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } catch (Exception e) {

            return "Error while sending mail!!!";
        }
    }
}