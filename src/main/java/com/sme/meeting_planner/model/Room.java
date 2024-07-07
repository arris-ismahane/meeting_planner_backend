package com.sme.meeting_planner.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class Room extends BasicEntity {
    private String name;
    private int capacity;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Equipement> initialEquipments;
}
