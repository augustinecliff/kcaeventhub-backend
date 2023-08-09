package tiketihub.emailconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

@Configuration
public class EmailConfig {
    @Autowired
    private MailSender mailSender;

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
}
