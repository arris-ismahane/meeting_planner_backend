package com.sme.meeting_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.SharedEquipment;
import java.util.Optional;

@Repository
public interface SharedEquipmentRepository extends JpaRepository<SharedEquipment, Long> {
  boolean existsByEquipmentId(long equipmentId);

  Optional<SharedEquipment> findOneByEquipment(Equipement equipment);

  @Query("""
          SELECT se
          FROM SharedEquipment se
          WHERE se.equipment.id = :equipmentId
            AND (
              SELECT COUNT(be)
              FROM Booking b
              JOIN b.bookedEquipements be
              WHERE be.equipment.id = se.equipment.id
                AND (
                  (:startDate BETWEEN b.startDate AND b.endDate)
                  OR (:endDate BETWEEN b.startDate AND b.endDate)
                  OR (b.startDate BETWEEN :startDate AND :endDate)
                  OR (b.endDate BETWEEN :startDate AND :endDate)
                )
            ) < se.total
      """)
  Optional<SharedEquipment> findAvailableEquipment(
      long equipmentId,
      long startDate,
      long endDate);

}
