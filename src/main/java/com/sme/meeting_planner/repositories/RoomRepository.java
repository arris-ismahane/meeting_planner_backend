package com.sme.meeting_planner.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sme.meeting_planner.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
            SELECT r FROM Room r
            WHERE CAST(CAST(r.capacity AS double) * :percentage AS int)  >= :nbParticipants
            AND r.id NOT IN (
                SELECT b.room.id FROM Booking b
                WHERE (
                    (:endDate > b.startDate - :gap AND :startDate < b.endDate + :gap)
                )
            )
                            """)
    List<Room> findRoomsNotBookedInRange(int nbParticipants, long startDate,
            long endDate, long gap, double percentage);
}
