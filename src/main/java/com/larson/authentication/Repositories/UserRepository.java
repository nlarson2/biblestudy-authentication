package com.larson.authentication.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larson.authentication.Security.User;

public interface UserRepository extends JpaRepository<User, Integer>{

    Optional<User> findByFirstname(String name);
    Optional<User> findByOauthID(String oauthID);
    
}