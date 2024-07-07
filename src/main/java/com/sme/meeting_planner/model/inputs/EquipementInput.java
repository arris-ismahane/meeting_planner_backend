package com.sme.meeting_planner.model.inputs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sme.meeting_planner.model.Equipement;

public record EquipementInput(
        String name) {
    @JsonIgnore
    public Equipement getEntity() {
        return Equipement.builder().name(name).build();
    }
}
