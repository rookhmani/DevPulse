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
            User admin = users.findByEmail("admin@devpulse.local")
                    .orElseGet(() -> User.builder()
                            .username("admin")
                            .email("admin@devpulse.local")
                            .build());
            admin.setUsername("admin");
            admin.setPasswordHash(encoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            users.save(admin);

            User viewer = users.findByEmail("viewer@devpulse.local")
                    .orElseGet(() -> User.builder()
                            .username("viewer")
                            .email("viewer@devpulse.local")
                            .build());
            viewer.setUsername("viewer");
            viewer.setPasswordHash(encoder.encode("viewer123"));
            viewer.setRole(UserRole.VIEWER);
            users.save(viewer);
        };
    }
}
