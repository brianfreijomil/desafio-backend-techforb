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
@Table(name = "readings")
public class ReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long value;

    @Column(name = "alert_type")
    @Enumerated(EnumType.STRING)
    private AlertTypeEnum alertType;

    @Column
    private LocalDateTime created_at;

    public ReadingEntity(Long value, AlertTypeEnum alert) {
        this.value = value;
        this.alertType = alert;
        this.created_at = LocalDateTime.now();
    }

    /*
    id (PK)
sensor_id (FK -> Sensores)
valor
fecha_hora
tipo_alerta (ok, media, roja)
     */
}
