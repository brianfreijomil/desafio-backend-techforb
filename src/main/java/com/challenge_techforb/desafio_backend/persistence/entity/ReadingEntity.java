package com.challenge_techforb.desafio_backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
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
    private Integer value;

    @Column(name = "alert_type")
    @Enumerated(EnumType.STRING)
    private AlertTypeEnum alertType;

    @Column
    private LocalDateTime created_at;

    public ReadingEntity(Integer value, AlertTypeEnum alert) {
        this.value = value;
        this.alertType = alert;
        this.created_at = LocalDateTime.now();
    }

}
