package com.sme.meeting_planner.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.exceptions.NoRoomAvailableException;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.model.inputs.MeetingInput;
import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.repositories.BookingRepository;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)

public class BookingServiceTest {
    @InjectMocks
    private BookingService service;
    @Mock
    private RoomService roomService;
    @Mock
    private MeetingRequirementService meetingRequirementService;
    @Mock
    private SharedEquipmentService sharedEquipmentService;
    @Mock
    private BookingRepository repository;

    private Booking expected;
    private MeetingInput input;
    private SharedEquipment sharedEquipement;
    private long id = 1l;

    @BeforeEach
    void setUp() {
        var equipement = Equipement.builder().id(1l).name("Screen").build();
        sharedEquipement = SharedEquipment.builder().id(1l).equipment(equipement).build();
        input = new MeetingInput("R1", 0, 0, 2, 5);
        expected = Booking
                .builder()
                .id(id)
                .name("Booking 1")
                .nbParticipants(5)
                .bookedEquipements(List.of(sharedEquipement))
                .build();
    }

    @Test
    void testCreateBooking() {
        var room = Room.builder().id(1l).initialEquipments(List.of()).build();
        var meetingRequirement = MeetingRequirement.builder().id(1l).requiredEquipements(List.of()).build();
        when(repository.save(any(Booking.class))).thenReturn(expected);
        when(roomService.getAvailableRooms(anyInt(), anyLong(), anyLong()))
                .thenReturn(List.of(room));

        when(meetingRequirementService.getMeetingRequirementById(anyLong())).thenReturn(meetingRequirement);
        Booking actual = service.makeReservation(input);

        Assertions.assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
        verify(repository).save(any());
        verify(meetingRequirementService).getMeetingRequirementById(anyLong());
        verify(roomService).getAvailableRooms(anyInt(), anyLong(), anyLong());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void testCreateBooking_No_Room_available() {
        when(roomService.getAvailableRooms(anyInt(), anyLong(), anyLong()))
                .thenReturn(List.of());

        assertThrows(NoRoomAvailableException.class, () -> service.makeReservation(input));
        verify(repository, never()).save(any());
        verify(meetingRequirementService, never()).getMeetingRequirementById(anyLong());
        verify(roomService).getAvailableRooms(anyInt(), anyLong(), anyLong());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void testDeleteBooking() {
        doNothing().when(repository).deleteById(anyLong());

        service.deleteBooking(anyLong());
        verify(repository).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetBookingById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));

        final var result = service.getBookingById(anyLong());
        Assertions.assertThat(result).isEqualTo(expected);
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetBookingById_should_throw_exception() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ElementNotFound.class, () -> service.getBookingById(1l));
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void testGetBookings() {

        Page<Booking> page = new PageImpl<>(List.of(expected));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(repository.findAll(pageableCaptor.capture())).thenReturn(page);
        var result = service.getBookings(any());

        assertEquals(1, result.size());
        assertTrue(result.contains(expected));
    }

}
