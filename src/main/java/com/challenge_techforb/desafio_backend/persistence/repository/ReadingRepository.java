package com.challenge_techforb.desafio_backend.persistence.repository;

import com.challenge_techforb.desafio_backend.persistence.entity.ReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<ReadingEntity,Long> {
}
