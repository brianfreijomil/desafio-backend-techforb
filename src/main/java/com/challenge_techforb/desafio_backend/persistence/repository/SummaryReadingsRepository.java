package com.challenge_techforb.desafio_backend.persistence.repository;

import com.challenge_techforb.desafio_backend.persistence.entity.SummaryReadingsEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryReadingsRepository extends JpaRepository<SummaryReadingsEntity,Long> {

    Optional<SummaryReadingsEntity> findByUser(UserEntity userEntity);
}
