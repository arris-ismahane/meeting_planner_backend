package com.sme.meeting_planner.model.inputs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sme.meeting_planner.model.Room;

public record RoomInput(
        String name,
        int capacity,
        List<Long> initialEquipmentIds) {
    @JsonIgnore
    public Room getEntity() {
        return Room
                .builder()
                .name(name)
                .capacity(capacity)
                .build();
    }
}
