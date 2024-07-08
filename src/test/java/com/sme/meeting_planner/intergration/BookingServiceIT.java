package com.sme.meeting_planner.intergration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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

import com.sme.meeting_planner.exceptions.ElementOutOfCapacity;
import com.sme.meeting_planner.exceptions.NoRoomAvailableException;
import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.model.enums.MeetingType;
import com.sme.meeting_planner.model.inputs.MeetingInput;
import com.sme.meeting_planner.repositories.BookingRepository;
import com.sme.meeting_planner.repositories.EquipementRepository;
import com.sme.meeting_planner.repositories.MeetingRequirementRepository;
import com.sme.meeting_planner.repositories.RoomRepository;
import com.sme.meeting_planner.repositories.SharedEquipmentRepository;
import com.sme.meeting_planner.services.BookingService;

import jakarta.transaction.Transactional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class BookingServiceIT {
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

    @Autowired
    private BookingService bookingService;

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
    public void testMakeReservation_should_return_booking() {
        // given
        long startDate = 1720256400000L;
        long endDate = 1720260000000L;

        Equipement equip = Equipement.builder().name("Board").build();
        equip = equipementRepository.save(equip);
        SharedEquipment board = SharedEquipment.builder().equipment(equip).total(2).build();
        board = repository.save(board);
        MeetingRequirement meetingReq = MeetingRequirement.builder().type(MeetingType.SPEC)
                .requiredEquipements(List.of(equip)).build();
        meetingReq = meetingRequirementRepository.save(meetingReq);
        Room room = Room
                .builder()
                .name("Room 1")
                .capacity(15)
                .initialEquipments(List.of())
                .build();
        room = roomRepository.save(room);

        MeetingInput input = new MeetingInput("Meeting 1", startDate, endDate, meetingReq.getId(), 10);
        Booking booking = bookingService.makeReservation(input);

        assertThat(booking).isNotNull();
        assertEquals(booking.getRoom().getId(), room.getId());
        // because the room doesnt have init equipements
        assertEquals(booking.getBookedEquipements().size(), meetingReq.getRequiredEquipements().size());
    }

    @Test
    public void testMakeReservation_should_throw_no_room_available_exception() {
        // given
        long startDate = 1720256400000L;
        long endDate = 1720260000000L;

        Equipement equip = Equipement.builder().name("Board").build();
        equip = equipementRepository.save(equip);
        SharedEquipment board = SharedEquipment.builder().equipment(equip).total(2).build();
        board = repository.save(board);
        MeetingRequirement meetingReq = MeetingRequirement.builder().type(MeetingType.SPEC)
                .requiredEquipements(List.of(equip)).build();
        meetingReq = meetingRequirementRepository.save(meetingReq);
        Room room = Room
                .builder()
                .name("Room 1")
                .capacity(7)
                .initialEquipments(List.of())
                .build();
        room = roomRepository.save(room);
        // room capacity < nbParticipants ==> no room is available
        MeetingInput input = new MeetingInput("Meeting 1", startDate, endDate, meetingReq.getId(), 10);

        assertThrows(NoRoomAvailableException.class, () -> bookingService.makeReservation(input));
    }

    @Test
    public void testMakeReservation_should_throw_element_out_of_capacity_exception() {
        // given
        long startDate = 1720256400000L;
        long endDate = 1720260000000L;

        Equipement equip = Equipement.builder().name("Board").build();
        equip = equipementRepository.save(equip);
        SharedEquipment shaEq = SharedEquipment.builder().equipment(equip).total(1).build();
        final SharedEquipment board = repository.save(shaEq);
        MeetingRequirement meetingReq = MeetingRequirement.builder().type(MeetingType.SPEC)
                .requiredEquipements(List.of(equip)).build();
        meetingReq = meetingRequirementRepository.save(meetingReq);
        Room room = Room
                .builder()
                .name("Room 1")
                .capacity(7)
                .initialEquipments(List.of())
                .build();
        room = roomRepository.save(room);
        Room room2 = Room
                .builder()
                .name("Room 2")
                .capacity(15)
                .initialEquipments(List.of())
                .build();
        room2 = roomRepository.save(room2);
        // book the BOARD
        MeetingInput input = new MeetingInput("Meeting 1", startDate, endDate, meetingReq.getId(), 4);

        Booking booking = bookingService.makeReservation(input);
        // Assert that the BOARD is booked
        assertTrue(booking.getBookedEquipements()
                .stream()
                .map(SharedEquipment::getId)
                .anyMatch(id -> id.equals(board.getId())));
        // Try to book it again
        MeetingInput input2 = new MeetingInput("Meeting 2", startDate, endDate, meetingReq.getId(), 7);
        assertThrows(ElementOutOfCapacity.class, () -> bookingService.makeReservation(input2));
    }

}
