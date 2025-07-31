// package com.example.insurance.domain.emailService.service;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;
// import com.example.insurance.domain.emailService.EmailUtils;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class EmailServiceImpl implements EmailService {
// private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account
// Verifcation";

// private final JavaMailSender sender;

// @Value("${spring.mail.verify.host}")
// private String host;
// @Value("${spring.mail.username}")
// private String fromEmail;

// @Override
// @Async
// public void sendNewAccountEmail(String name, String email, String token) {
// try {
// var message = new SimpleMailMessage();

// message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
// message.setFrom(fromEmail);
// message.setTo(email);
// message.setText(EmailUtils.getEmailMessage(name, host, token));
// sender.send(message);

// } catch (Exception e) {
// log.error("Failed to send email to {}. Error: {}", email, e.getMessage());
// throw new RuntimeException("Unable to send email");
// }
// }

// }
