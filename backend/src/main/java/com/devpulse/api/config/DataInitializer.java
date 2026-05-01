package com.devpulse.api.config;

import com.devpulse.api.entity.User;
import com.devpulse.api.enums.UserRole;
import com.devpulse.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    @Bean
    CommandLineRunner seedDefaultUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail("admin@devpulse.local").isEmpty()) {
                users.save(User.builder()
                        .username("admin")
                        .email("admin@devpulse.local")
                        .passwordHash(encoder.encode("admin123"))
                        .role(UserRole.ADMIN)
                        .build());
            }
            if (users.findByEmail("viewer@devpulse.local").isEmpty()) {
                users.save(User.builder()
                        .username("viewer")
                        .email("viewer@devpulse.local")
                        .passwordHash(encoder.encode("viewer123"))
                        .role(UserRole.VIEWER)
                        .build());
            }
        };
    }
}
