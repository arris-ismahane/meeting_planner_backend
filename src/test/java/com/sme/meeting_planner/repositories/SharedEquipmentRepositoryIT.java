package com.sme.meeting_planner.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.SharedEquipment;

@DataJpaTest
@ActiveProfiles("test")
public class SharedEquipmentRepositoryIT {
    @Autowired
    private SharedEquipmentRepository repository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private RoomRepository roomRepository;

    private SharedEquipment screen, board;
    private Room room;
    private Booking booking;
    private long startDate, endDate;

    @BeforeEach
    void setUp() {
        startDate = 1720256400000L;
        endDate = 1720260000000L;
        var equip1 = Equipement.builder().name("Screen").build();
        equip1 = equipementRepository.save(equip1);
        var sharedEquipment1 = SharedEquipment.builder().equipment(equip1).total(1).build();
        screen = repository.save(sharedEquipment1);
        var equip2 = Equipement.builder().name("Board").build();
        var sharedEquipment2 = SharedEquipment.builder().equipment(equip2).total(2).build();
        board = repository.save(sharedEquipment2);
        room = Room
                .builder()
                .capacity(15)
                .initialEquipments(List.of())
                .build();
        room = roomRepository.save(room);
        booking = Booking
                .builder()
                .room(room)
                .startDate(startDate)
                .endDate(endDate)
                .bookedEquipements(List.of(screen, board))
                .build();
        bookingRepository.save(booking);

    }

    @Test
    void testFindAvailableEquipment() {
        // screen booked ---- board available
        Optional<SharedEquipment> screenResult = repository.findAvailableEquipment(screen.getId(), startDate, endDate);
        Optional<SharedEquipment> boardResult = repository.findAvailableEquipment(board.getId(), startDate, endDate);
        assertFalse(screenResult.isPresent());
        assertTrue(boardResult.isPresent());
    }

}
