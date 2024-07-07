package com.sme.meeting_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sme.meeting_planner.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(long startDate, long endDate);
}
