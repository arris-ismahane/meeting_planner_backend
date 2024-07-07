package com.sme.meeting_planner.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.Room;

@DataJpaTest
@ActiveProfiles("test")
public class RoomRepositoryIT {
        @Autowired
        private RoomRepository roomRepository;

        @Autowired
        private BookingRepository bookingRepository;

        private Booking booking;
        private Room room1, room2;
        private long startDate, endDate;
        private List<Room> availableRooms;

        @BeforeEach
        void setUp() {
                startDate = 1720256400000L;
                endDate = 1720260000000L;
                room1 = Room
                                .builder()
                                .capacity(15)
                                .initialEquipments(List.of())
                                .build();
                room1 = roomRepository.save(room1);
                room2 = Room
                                .builder()
                                .capacity(7)
                                .initialEquipments(List.of())
                                .build();
                room2 = roomRepository.save(room2);

                booking = Booking
                                .builder()
                                .room(room1)
                                .startDate(startDate)
                                .endDate(endDate)
                                .build();
                bookingRepository.save(booking);
        }

        @Test
        void testFindRoomsNotBookedInRange() {
                availableRooms = new ArrayList<>();
                long hour = 3600000L;
                long start = startDate + hour;
                long end = startDate + 2 * hour;
                double capacity = 0.7;
                int nbParticipants = 4;

                // Room1 booked ---- Room2 available
                availableRooms = roomRepository.findRoomsNotBookedInRange(nbParticipants,
                                start, end, hour, capacity);
                Assertions.assertThat(availableRooms).isNotNull();
                Assertions.assertThat(availableRooms.size()).isEqualTo(1);
                assertEquals(room2.getId(), availableRooms.get(0).getId());
                assertTrue(availableRooms.stream().filter(e -> e.getId() == room1.getId()).toList().isEmpty());

                // Room1 booked ---- Room2 not enough capacity
                nbParticipants = 6;
                availableRooms = new ArrayList<>();
                availableRooms = roomRepository.findRoomsNotBookedInRange(nbParticipants,
                                start, end, hour, capacity);
                Assertions.assertThat(availableRooms).isNotNull();
                Assertions.assertThat(availableRooms.size()).isEqualTo(0);

                // Room1 available ---- Room2 available
                start = startDate + 2 * hour;
                end = startDate + 3 * hour;
                nbParticipants = 4;
                availableRooms = new ArrayList<>();
                availableRooms = roomRepository.findRoomsNotBookedInRange(nbParticipants,
                                start, end, hour, capacity);
                Assertions.assertThat(availableRooms).isNotNull();
                Assertions.assertThat(availableRooms.size()).isEqualTo(2);

        }

}
