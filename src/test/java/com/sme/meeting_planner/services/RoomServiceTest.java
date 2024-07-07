package com.sme.meeting_planner.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.inputs.RoomInput;
import com.sme.meeting_planner.repositories.RoomRepository;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)

public class RoomServiceTest {
    @InjectMocks
    private RoomService service;
    @Mock
    private EquipementService equipementService;
    @Mock
    private RoomRepository repository;

    private Room expected;
    private RoomInput input;
    private Equipement equipement;
    private long id = 1l;

    @BeforeEach
    void setUp() {
        equipement = Equipement.builder().id(1l).name("Screen").build();
        input = new RoomInput("Room 1", 7, List.of(1l));
        expected = Room
                .builder()
                .id(id)
                .name("Room 1")
                .capacity(7)
                .initialEquipments(List.of(equipement))
                .build();
    }

    @Test
    void testCreateRoom() {
        when(repository.save(any(Room.class))).thenReturn(expected);

        Room actual = service.createRoom(input);

        Assertions.assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
        verify(repository).save(any());
        verify(equipementService).getEquipementsByIds(anyList());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void testDeleteRoom() {
        doNothing().when(repository).deleteById(anyLong());

        service.deleteRoom(anyLong());
        verify(repository).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetRoomById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));

        final var result = service.getRoomById(anyLong());
        Assertions.assertThat(result).isEqualTo(expected);
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetRoomById_should_throw_exception() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ElementNotFound.class, () -> service.getRoomById(1l));
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void testGetRooms() {
        Room room2 = Room.builder().id(2l).capacity(6).build();
        List<Room> list = List.of(expected, room2);

        when(repository.findAll()).thenReturn(list);
        List<Room> result = service.getRooms(-1, -1);

        assertEquals(list.size(), result.size());
        assertTrue(result.contains(room2));
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testUpdateRoom() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(repository.save(any(Room.class))).thenReturn(expected);

        Room updated = service.updateRoom(id, input);
        assertEquals(updated.getId(), expected.getId());
        verify(equipementService).getEquipementsByIds(anyList());
        verify(repository).findById(anyLong());
        verify(repository).save(any());
    }
}
