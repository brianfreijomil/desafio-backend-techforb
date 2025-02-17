package com.challenge_techforb.desafio_backend.controller.dto.response;

import com.challenge_techforb.desafio_backend.persistence.entity.AlertTypeEnum;
import com.challenge_techforb.desafio_backend.persistence.entity.ReadingEntity;
import com.challenge_techforb.desafio_backend.persistence.repository.IAllPlantsAndSumReadingsByAlertType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReadingOut {

    private Long id;
    private Integer value;
    private AlertTypeEnum type;

    public ReadingOut(IAllPlantsAndSumReadingsByAlertType i) {
        this.value = i.getTotalValue();
        this.type = AlertTypeEnum.valueOf(i.getAlertType());
    }

    public ReadingOut(ReadingEntity re) {
        this.id = re.getId();
        this.value = re.getValue();
        this.type = re.getAlertType();
    }
}
