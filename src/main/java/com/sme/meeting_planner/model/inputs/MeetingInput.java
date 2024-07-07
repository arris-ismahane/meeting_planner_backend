package com.sme.meeting_planner.model.inputs;

public record MeetingInput(
                String name,
                long startDate,
                long endDate,
                long typeId,
                int nbParticipants) {

}
