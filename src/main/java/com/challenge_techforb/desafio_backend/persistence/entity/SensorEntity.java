package com.challenge_techforb.desafio_backend.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sensors")
public class SensorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_name")
    @Enumerated(EnumType.STRING)
    private SensorEnum type;

    @Column
    private LocalDateTime created_at;

    public SensorEntity(SensorEnum type) {
        this.type = type;
        this.created_at = LocalDateTime.now();
    }
}
