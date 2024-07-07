package com.sme.meeting_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sme.meeting_planner.model.Equipement;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, Long> {

}
