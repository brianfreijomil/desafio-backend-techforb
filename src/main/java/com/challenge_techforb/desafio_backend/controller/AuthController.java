package com.challenge_techforb.desafio_backend.controller;

import com.challenge_techforb.desafio_backend.controller.dto.request.AuthCreateUserIn;
import com.challenge_techforb.desafio_backend.controller.dto.request.AuthLoginIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.AuthResponse;
import com.challenge_techforb.desafio_backend.controller.dto.response.UserInfoOut;
import com.challenge_techforb.desafio_backend.service.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserDetailServiceImpl userDetailService;

    private AuthController(UserDetailServiceImpl userDetailServiceImpl) {
        this.userDetailService = userDetailServiceImpl;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthCreateUserIn userRequest){
        return new ResponseEntity<>(this.userDetailService.createUser(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginIn userRequest){
        return new ResponseEntity<>(this.userDetailService.loginUser(userRequest), HttpStatus.OK);
    }

    @GetMapping("/me/{username}")
    public ResponseEntity<UserInfoOut> getInfo(@PathVariable String username){
        return new ResponseEntity<>(this.userDetailService.getUserInfo(username), HttpStatus.OK);
    }
}
