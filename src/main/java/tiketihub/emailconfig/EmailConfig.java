package tiketihub.emailconfig;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

@Configuration
public class EmailConfig {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String issuer;

    @Async("emailTaskExecutor") //Run this method asynchroously using the task executor bean
    public void sendSimpleEmail(String receipient,
                                String subject,
                                String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(issuer);
        email.setTo(receipient);
        email.setSentDate(new Date());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    @Async("emailTaskExecutor")
    public void sendMailWithAttachment(String receipient,
                                       String subject,
                                       String html)throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(receipient);
        helper.setSubject(subject);
        helper.setText(html, true);

       /* ByteArrayResource attachmentResource = new ByteArrayResource(attachmentData);
        helper.addAttachment(attachmentFile, attachmentResource);*/
        mailSender.send(message);

    }

}
