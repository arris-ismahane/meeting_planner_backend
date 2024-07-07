package com.sme.meeting_planner.intergration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.repositories.BookingRepository;
import com.sme.meeting_planner.repositories.RoomRepository;

import jakarta.transaction.Transactional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RoomRepositoryIT {
        @Container
        @ServiceConnection
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-bullseye");

        @Autowired
        RoomRepository roomRepository;

        @Autowired
        private BookingRepository bookingRepository;

        @BeforeAll
        static void beforeAll() {
                postgres.start();
        }

        @AfterAll
        static void afterAll() {
                postgres.stop();
        }

        @BeforeEach
        void setUp() {
                roomRepository.deleteAll();
                bookingRepository.deleteAll();
        }

        @Test
        void testFindRoomsNotBookedInRange_should_find_One_available() {
                // given
                long startDate = 1720256400000L;
                long endDate = 1720260000000L;
                var r1 = Room
                                .builder()
                                .capacity(15)
                                .initialEquipments(List.of())
                                .build();
                final Room room1 = roomRepository.save(r1);
                Room r2 = Room
                                .builder()
                                .capacity(7)
                                .initialEquipments(List.of())
                                .build();
                final Room room2 = roomRepository.save(r2);

                Booking booking = Booking
                                .builder()
                                .room(room1)
                                .startDate(startDate)
                                .endDate(endDate)
                                .build();
                bookingRepository.save(booking);
                // wehen
                long hour = 3600000L;
                long start = startDate + hour;
                long end = startDate + 2 * hour;
                double capacity = 0.7;
                int nbParticipants = 4;

                // Room1 booked ---- Room2 available
                List<Room> availableRooms = roomRepository.findRoomsNotBookedInRange(nbParticipants,
                                start, end, hour, capacity);
                Assertions.assertThat(availableRooms).isNotNull();
                Assertions.assertThat(availableRooms.size()).isEqualTo(1);
                assertEquals(room2.getId(), availableRooms.get(0).getId());
                assertTrue(availableRooms.stream().filter(e -> e.getId() == room1.getId()).toList().isEmpty());

        }

        @Test
        void testFindRoomsNotBookedInRange_should_Not_find_available() {
                // given
                long startDate = 1720256400000L;
                long endDate = 1720260000000L;
                var r1 = Room
                                .builder()
                                .capacity(15)
                                .initialEquipments(List.of())
                                .build();
                final Room room1 = roomRepository.save(r1);
                Room r2 = Room
                                .builder()
                                .capacity(7)
                                .initialEquipments(List.of())
                                .build();
                roomRepository.save(r2);

                Booking booking = Booking
                                .builder()
                                .room(room1)
                                .startDate(startDate)
                                .endDate(endDate)
                                .build();
                bookingRepository.save(booking);
                // wehen
                long hour = 3600000L;
                long start = startDate + hour;
                long end = startDate + 2 * hour;
                double capacity = 0.7;
                int nbParticipants = 4;

                // Room1 booked ---- Room2 not enough capacity
                nbParticipants = 6;
                var availableRooms = roomRepository.findRoomsNotBookedInRange(nbParticipants,
                                start, end, hour, capacity);
                Assertions.assertThat(availableRooms).isNotNull();
                Assertions.assertThat(availableRooms.size()).isEqualTo(0);

        }

        @Test
        void testFindRoomsNotBookedInRange_should_find_two_available() {
                // given
                long startDate = 1720256400000L;
                long endDate = 1720260000000L;
                var r1 = Room
                                .builder()
                                .capacity(15)
                                .initialEquipments(List.of())
                                .build();
                final Room room1 = roomRepository.save(r1);
                Room r2 = Room
                                .builder()
                                .capacity(7)
                                .initialEquipments(List.of())
                                .build();
                roomRepository.save(r2);

                Booking booking = Booking
                                .builder()
                                .room(room1)
                                .startDate(startDate)
                                .endDate(endDate)
                                .build();
                bookingRepository.save(booking);
                // wehen
                long hour = 3600000L;
                long start = startDate + 2 * hour;
                long end = startDate + 3 * hour;
                double capacity = 0.7;
                int nbParticipants = 4;

                // Room1 available ---- Room2 available
                start = end = nbParticipants = 4;
                var availableRooms = roomRepository.findRoomsNotBookedInRange(nbParticipants,
                                start, end, hour, capacity);
                Assertions.assertThat(availableRooms).isNotNull();
                Assertions.assertThat(availableRooms.size()).isEqualTo(2);

        }

}
