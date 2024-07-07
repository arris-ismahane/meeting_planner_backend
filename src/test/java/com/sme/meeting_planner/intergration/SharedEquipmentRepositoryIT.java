package com.sme.meeting_planner.intergration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

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
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.repositories.BookingRepository;
import com.sme.meeting_planner.repositories.EquipementRepository;
import com.sme.meeting_planner.repositories.MeetingRequirementRepository;
import com.sme.meeting_planner.repositories.RoomRepository;
import com.sme.meeting_planner.repositories.SharedEquipmentRepository;

import jakarta.transaction.Transactional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class SharedEquipmentRepositoryIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-bullseye");

    @Autowired
    private SharedEquipmentRepository repository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MeetingRequirementRepository meetingRequirementRepository;

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

        // delete all data that may have beeing added on init (like from json files)
        bookingRepository.deleteAll();
        meetingRequirementRepository.deleteAll();
        roomRepository.deleteAll();
        repository.deleteAll();
        equipementRepository.deleteAll();
    }

    @Test
    void testFindAvailableEquipment_should_not_find_available() {
        // given
        long startDate = 1720256400000L;
        long endDate = 1720260000000L;

        var equip1 = Equipement.builder().name("Screen").build();
        equip1 = equipementRepository.save(equip1);
        var sharedEquipment1 = SharedEquipment.builder().equipment(equip1).total(1).build();
        SharedEquipment screen = repository.save(sharedEquipment1);
        var equip2 = Equipement.builder().name("Board").build();
        var sharedEquipment2 = SharedEquipment.builder().equipment(equip2).total(2).build();
        SharedEquipment board = repository.save(sharedEquipment2);
        Room room = Room
                .builder()
                .capacity(15)
                .initialEquipments(List.of())
                .build();
        room = roomRepository.save(room);
        Booking booking = Booking
                .builder()
                .room(room)
                .startDate(startDate)
                .endDate(endDate)
                .bookedEquipements(List.of(screen, board))
                .build();
        bookingRepository.save(booking);

        // screen fully booked
        Optional<SharedEquipment> screenResult = repository.findAvailableEquipment(screen.getId(), startDate, endDate);
        assertFalse(screenResult.isPresent());

    }

    @Test
    void testFindAvailableEquipment_should_find_available() {
        // given
        long startDate = 1720256400000L;
        long endDate = 1720260000000L;

        var equip1 = Equipement.builder().name("Screen").build();
        equip1 = equipementRepository.save(equip1);
        var sharedEquipment1 = SharedEquipment.builder().equipment(equip1).total(1).build();
        SharedEquipment screen = repository.save(sharedEquipment1);
        var equip2 = Equipement.builder().name("Board").build();
        var sharedEquipment2 = SharedEquipment.builder().equipment(equip2).total(2).build();
        SharedEquipment board = repository.save(sharedEquipment2);
        Room room = Room
                .builder()
                .capacity(15)
                .initialEquipments(List.of())
                .build();
        room = roomRepository.save(room);
        Booking booking = Booking
                .builder()
                .room(room)
                .startDate(startDate)
                .endDate(endDate)
                .bookedEquipements(List.of(screen, board))
                .build();
        bookingRepository.save(booking);

        // board available
        Optional<SharedEquipment> boardResult = repository.findAvailableEquipment(board.getId(), startDate, endDate);
        assertTrue(boardResult.isPresent());
    }

}
