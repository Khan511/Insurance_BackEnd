// package com.example.insurance.domain.emailService;

// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;

// import com.example.insurance.domain.emailService.service.EmailService;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class UserEventListner {

// private final EmailService emailService;

// @EventListener
// public void onUserEvent(UserEvent userEvent) {
// switch (userEvent.getEventType()) {
// case REGISTRATION ->
// emailService.sendNewAccountEmail(userEvent.getUser().getName().getFirstName(),
// userEvent.getUser().getEmail(), (String) userEvent.getData().get("key"));
// default -> {
// }
// }
// }
// }
