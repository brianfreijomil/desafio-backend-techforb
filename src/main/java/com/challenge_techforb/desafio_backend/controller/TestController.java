package com.challenge_techforb.desafio_backend.controller;

import com.challenge_techforb.desafio_backend.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private TestService testService;

    public TestController(TestService srv) {
        this.testService = srv;
    }

    @GetMapping("/{username}")
    public ResponseEntity<String> getByUsername(@PathVariable String username) {
        return new ResponseEntity<>(this.testService.getByUsername(username).getUsername(), HttpStatus.OK);
    }
}
