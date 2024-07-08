package com.sme.meeting_planner.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking extends BasicEntity {
    private String name;
    private long startDate;
    private long endDate;
    @ManyToOne
    private MeetingRequirement type;
    private int nbParticipants;
    @ManyToOne
    private Room room;
    @ManyToMany
    private List<SharedEquipment> bookedEquipements;
}
