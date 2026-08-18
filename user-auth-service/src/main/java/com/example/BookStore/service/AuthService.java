package com.example.BookStore.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.BookStore.dto.AuthResponse;
import com.example.BookStore.dto.ForgotPasswordRequest;
import com.example.BookStore.dto.LoginRequest;
import com.example.BookStore.dto.RegisterAdminRequest;
import com.example.BookStore.dto.RegisterRequest;
import com.example.BookStore.dto.ResetPasswordRequest;
import com.example.BookStore.dto.UpdateProfileRequest;
import com.example.BookStore.dto.VerifyOtpRequest;
import com.example.BookStore.entity.Role;
import com.example.BookStore.entity.User;
import com.example.BookStore.exception.EmailAlreadyExistsException;
import com.example.BookStore.exception.InvalidCredentialsException;
import com.example.BookStore.repository.UserRepository;
import com.example.BookStore.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    private static final int OTP_VALID_MINUTES = 2;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(hashedPassword)
                .role(Role.USER)
                .enabled(true)
                .build();

        user.prePersistDefaults();
        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .message("Registration successful. Please login to continue.")
                .build();
    }

    public AuthResponse registerAdmin(RegisterAdminRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User admin = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(hashedPassword)
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        admin.prePersistDefaults();
        User savedAdmin = userRepository.save(admin);

        return AuthResponse.builder()
                .id(savedAdmin.getId())
                .fullName(savedAdmin.getFullName())
                .email(savedAdmin.getEmail())
                .role(savedAdmin.getRole())
                .message("Admin account created successfully.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("This account has been disabled");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("No account found with this email"));

        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    /**
     * Checks the OTP is correct and not expired, WITHOUT consuming it or
     * changing anything - lets the frontend show a "verified" checkmark
     * before asking for the new password. The actual reset (below) re-checks
     * the OTP again independently, so this step is purely for UX feedback.
     */
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid request"));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("OTP has expired. Please request a new one.");
        }
        // Valid - nothing to change yet, just confirming to the caller.
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid request"));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("OTP has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    /**
     * Updates the logged-in user's own name and/or profile picture.
     * Fields left null in the request are left unchanged.
     */
    public AuthResponse updateProfile(String email, UpdateProfileRequest request) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("Account not found"));

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
        user.setFullName(request.getFullName());
    }
    // profileImage: null = leave unchanged, "" (empty string) = remove photo,
    // otherwise = set to the new base64 image
    if (request.getProfileImage() != null) {
        user.setProfileImage(request.getProfileImage().isEmpty() ? null : request.getProfileImage());
    }

        User saved = userRepository.save(user);

        return AuthResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .profileImage(saved.getProfileImage())
                .role(saved.getRole())
                .message("Profile updated successfully")
                .build();
    }

    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Account not found"));
        userRepository.delete(user);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}