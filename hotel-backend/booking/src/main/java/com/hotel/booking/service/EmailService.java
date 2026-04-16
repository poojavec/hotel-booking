package com.hotel.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService sends real emails to users using Spring Mail + Gmail SMTP.
 * Logger is used to print info/error messages to the server console (not to the user).
 */
@Service
public class EmailService {

    // Logger prints messages to the server console — useful for debugging
    // WHY use Logger instead of System.out.println? Logger adds timestamp, class name, log level.
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender; // Spring's built-in email sender — configured in application.properties

    /**
     * Sends a welcome email when a new user registers.
     */
    public void sendRegistrationEmail(String toEmail, String name) {
        try {
            // SimpleMailMessage = a plain text email (no HTML)
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to Ethereal Midnight");
            message.setText("Hello " + name + ",\n\nWelcome to Ethereal Midnight! Your luxury journey begins now.");
            mailSender.send(message); // Actually sends the email via Gmail SMTP
            logger.info("Welcome email sent to {}", toEmail);
        } catch (Exception e) {
            // If email fails, we just log the error — we don't crash the whole app
            logger.error("Error sending welcome email: {}", e.getMessage());
        }
    }

    /**
     * Sends a booking confirmation email after a successful booking.
     */
    public void sendBookingConfirmationEmail(String toEmail, String name, String confirmationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your Luxury Stay is Confirmed!");
            message.setText("Hello " + name + ",\n\nYour stay is confirmed!\n\nConfirmation Code: " + confirmationCode + "\n\nWe look forward to seeing you.");
            mailSender.send(message);
            logger.info("Booking confirmation email sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Error sending booking confirmation email: {}", e.getMessage());
        }
    }

    /**
     * Sends a cancellation email when a booking is cancelled.
     */
    public void sendCancellationEmail(String toEmail, String name, String confirmationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Booking Cancellation Notice");
            message.setText("Hello " + name + ",\n\nYour booking (" + confirmationCode + ") has been cancelled.\n\nWe hope to see you again soon.");
            mailSender.send(message);
            logger.info("Cancellation email sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Error sending cancellation email: {}", e.getMessage());
        }
    }
}
