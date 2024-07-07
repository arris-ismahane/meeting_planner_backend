package com.sme.meeting_planner.model;

import java.util.List;

import com.sme.meeting_planner.model.enums.MeetingType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class MeetingRequirement extends BasicEntity {
    @Column(unique = true, name = "meeting_type")
    @Enumerated(EnumType.STRING)
    private MeetingType type;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Equipement> requiredEquipements;

}
