package com.challenge_techforb.desafio_backend.persistence.repository;

public interface IAllPlantsAndSumReadingsByAlertType {
    public Long getId();
    public String getAlertType();
    public Integer getTotalValue();
}
