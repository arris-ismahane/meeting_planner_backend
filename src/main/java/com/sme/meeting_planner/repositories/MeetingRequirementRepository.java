package com.sme.meeting_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sme.meeting_planner.model.MeetingRequirement;
import java.util.Optional;

import com.sme.meeting_planner.model.enums.MeetingType;

@Repository
public interface MeetingRequirementRepository extends JpaRepository<MeetingRequirement, Long> {
    boolean existsByType(MeetingType type);

    Optional<MeetingRequirement> findOneByType(MeetingType type);
}
