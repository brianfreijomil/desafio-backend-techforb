package com.challenge_techforb.desafio_backend.service;

import com.challenge_techforb.desafio_backend.persistence.entity.UserEntity;
import com.challenge_techforb.desafio_backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private UserRepository repository;

    public TestService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public UserEntity getByUsername(String username) {
        return this.repository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));
    }

}
