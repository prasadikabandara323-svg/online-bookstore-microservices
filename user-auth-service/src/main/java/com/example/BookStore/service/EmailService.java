package com.example.BookStore.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Password Reset Code - Online Bookstore");
        message.setText("Your OTP code is: " + otp + "\n\nThis code expires in 10 minutes.\nIf you didn't request this, please ignore this email.");
        mailSender.send(message);
    }
}