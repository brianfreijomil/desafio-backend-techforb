package com.challenge_techforb.desafio_backend.controller;

import com.challenge_techforb.desafio_backend.controller.dto.request.SensorIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.SensorOut;
import com.challenge_techforb.desafio_backend.service.SensorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sensors")
public class SensorController {

    private final SensorsService sensorsService;

    @GetMapping("/by-plant/{plantId}")
    public ResponseEntity<List<SensorOut>> getAllSensorsByPlant(@PathVariable Long plantId) {
        return new ResponseEntity<>(this.sensorsService.getAllSensorsByPlant(plantId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorOut> updateSensor(@PathVariable Long id, @RequestBody @Valid SensorIn requestBody) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(this.sensorsService.updateSensor(id, requestBody, username),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> disableEnableSensor(@PathVariable Long id) {
        this.sensorsService.disableEnableSensor(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
