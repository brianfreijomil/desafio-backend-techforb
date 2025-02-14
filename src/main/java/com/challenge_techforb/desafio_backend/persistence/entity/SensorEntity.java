package com.challenge_techforb.desafio_backend.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToOne
    private PlantEntity plant;

    @Column
    private Boolean isEnabled;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<ReadingEntity> readings = new HashSet<>();

    @Column
    private LocalDateTime created_at;

    public SensorEntity(SensorEnum type,PlantEntity plant) {
        this.type = type;
        this.created_at = LocalDateTime.now();
        this.isEnabled = true;
        this.plant = plant;
    }
}
