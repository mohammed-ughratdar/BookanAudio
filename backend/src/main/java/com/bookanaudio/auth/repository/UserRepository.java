package com.bookanaudio.auth.repository;

import com.bookanaudio.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
