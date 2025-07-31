package com.example.insurance.domain.emailService.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendNewAccountEmail(String name, String email, String token);
}
