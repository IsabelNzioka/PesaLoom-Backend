package com.lendswift.lendswift.user;

import com.lendswift.lendswift.exception.EmailAlreadyInUseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    @Transactional
    public User register(String email, String rawPassword, String firstName, String lastName) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException();
        }
        User user = new User(normalizedEmail, passwordEncoder.encode(rawPassword), firstName.trim(), lastName.trim());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, String newRawPassword) {
        user.changePassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }
}
