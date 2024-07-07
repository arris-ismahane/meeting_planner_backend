package com.sme.meeting_planner.model.inputs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sme.meeting_planner.model.SharedEquipment;

public record SharedEquipmentInput(
        long equipmentId,
        int total) {
    @JsonIgnore
    public SharedEquipment getEntity() {
        return SharedEquipment
                .builder()
                .total(total)
                .build();
    }

}
