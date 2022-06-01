package group2.monopoly.mail;

/**
 * Service interface for sending plaintext emails.
 */
public interface EmailService {
    /**
     * Sends a plaintext email.
     *
     * @param to      receiver of the email
     * @param subject subject of the email
     * @param text    body of the email
     */
    void sendSimpleMessage(String to,
                           String subject,
                           String text);
}
