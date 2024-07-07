package com.sme.meeting_planner.model.inputs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.enums.MeetingType;

public record MeetingRequirementInput(
        MeetingType type,
        List<Long> requiredEquipementIds) {
    @JsonIgnore
    public MeetingRequirement getEntity() {
        return MeetingRequirement
                .builder()
                .type(type)
                .build();
    }
}
