package group2.monopoly.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private static final String NO_REPLY_ADDRESS = "no-reply@koluacik.xyz";

    @Autowired
    private JavaMailSender mailSender; // = new JavaMailSenderImpl();


    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {

            log.info("to " + to);
            log.info("subject " + subject);
            log.info("text " + text);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NO_REPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            log.info("message " + message);
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
