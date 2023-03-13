package backend.email;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.swing.text.html.HTML;


@Service
public class EmailServiceImpl implements EmailService {
    private final String msgBody = "Your registration is: http://localhost/user/verifyRegistration?token=";
    private final String subject = "Verify your registration";
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender javaMailSender, @Qualifier("customTemplateEngine") TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }


    private String build(String message) {
        Context context = new Context();
        context.setVariable("link", message);
        return templateEngine.process("email_confirmation", context);
    }

    public void sendSimpleMail(String sendTo, String token) {
        try {
            String content = build(msgBody + token);
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom("springboot@support.com");
            mailMessage.setText("Your registration is: http://localhost/user/verifyRegistration?token=" + token);
            mailMessage.setSubject(subject);
            mailMessage.setTo(sendTo);
            mailMessage.setSentDate(new java.util.Date());
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
            String content = build(msgBody + token);
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("springboot@support.com");
            mimeMessageHelper.setTo(sendTo);
            mimeMessageHelper.setText(content);
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