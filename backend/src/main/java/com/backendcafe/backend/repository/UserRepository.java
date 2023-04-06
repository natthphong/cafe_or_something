package com.backendcafe.backend.repository;

import com.backendcafe.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User , Integer> {

        User findByEmailId(@Param("email") String email);
}
