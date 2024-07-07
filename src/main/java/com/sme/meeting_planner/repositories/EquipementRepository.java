package com.sme.meeting_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sme.meeting_planner.model.Equipement;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

}
