package com.example.sociallogin.repository;

import com.example.sociallogin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByUsername(String username);

}
