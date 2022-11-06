package com.example.sociallogin;

import com.example.sociallogin.oauth.enums.AuthenticationType;
import com.example.sociallogin.entity.User;
import com.example.sociallogin.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SocialLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialLoginApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunnerBean(UserRepository userRepository) {
        return (args) -> {
            User newUser = new User();
            newUser.setUsername("joecole@icloud.com");
            newUser.setPassword("$2a$04$I9Q2sDc4QGGg5WNTLmsz0.fvGv3OjoZyj81PrSFyGOqMphqfS2qKu");
            newUser.setAuthType(AuthenticationType.DATABASE);
            newUser.setAuthorities(new String[]{"read"});

            userRepository.save(newUser);
        };
    }

}
