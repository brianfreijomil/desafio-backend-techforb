package com.challenge_techforb.desafio_backend.controller;

import com.challenge_techforb.desafio_backend.controller.dto.request.PlantIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.AllSensorsReadingsStatsOut;
import com.challenge_techforb.desafio_backend.controller.dto.response.PlantInfoOut;
import com.challenge_techforb.desafio_backend.service.PlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/plants")
public class PlantController {

    private final PlantService plantService;

    //just for admins...
    @GetMapping("")
    public ResponseEntity<List<PlantInfoOut>> getAllPlants() {
        return new ResponseEntity<>(this.plantService.getAllPlants(), HttpStatus.OK);
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<PlantInfoOut>> getAllPlantsByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(this.plantService.getAllPlantsByUser(username), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantInfoOut> getPlantDetail(@PathVariable Long id) {
        return new ResponseEntity<>(this.plantService.getPlantDetailById(id), HttpStatus.OK);
    }

    @GetMapping("/by-user/summary-readings")
    public ResponseEntity<AllSensorsReadingsStatsOut> getSummaryReadings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(this.plantService.getAllSensorsReadingsByUser(username), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<PlantInfoOut> createPlant(@RequestBody @Valid PlantIn requestBody) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(this.plantService.createPlant(requestBody, username), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantInfoOut> updatePlant(@PathVariable Long id, @RequestBody @Valid PlantIn requestBody) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(this.plantService.updatePlant(id, requestBody, username), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePlant(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        this.plantService.deletePlant(id, username);
        return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
    }


}
