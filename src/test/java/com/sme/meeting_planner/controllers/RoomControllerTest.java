package com.sme.meeting_planner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.BasicControllerTest;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.inputs.RoomInput;
import com.sme.meeting_planner.services.RoomService;
import java.util.List;

@WebMvcTest(RoomController.class)

public class RoomControllerTest extends BasicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService service;

    @Test
    void testCreateRoom() throws Exception {
        var input = new RoomInput("Room 1", 25, List.of());
        var expected = Room.builder().id(1l).name("Room 1").build();
        when(service.createRoom(any())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/admin/room")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testDeleteRoom() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/admin/room/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllRooms() throws Exception {
        List<Room> rooms = List.of(
                Room
                        .builder()
                        .id(1l)
                        .name("Room 1")
                        .initialEquipments(List.of(
                                Equipement.builder().id(1L).name("Screen").build(),
                                Equipement.builder().id(2L).name("Board").build()))
                        .build(),
                Room
                        .builder()
                        .id(2l)
                        .name("Room 2")
                        .initialEquipments(List.of())
                        .build());

        when(service.getRooms(anyInt(), anyInt())).thenReturn(rooms);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/room")
                        .with(csrf())
                        .param("index", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(rooms.size()));
        verify(service).getRooms(anyInt(), anyInt());
    }

    @Test
    void testGetRoom() throws Exception {
        long id = 2L;
        var expected = Room
                .builder()
                .id(2l)
                .name("Room 2")
                .initialEquipments(List.of())
                .build();

        Mockito.when(service.getRoomById(anyLong())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/room/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetRoomsCount() throws Exception {
        long count = 3;
        Mockito.when(service.getRoomsCount()).thenReturn(count);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/room/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").value(count));

        Mockito.verify(service).getRoomsCount();
    }

    @Test
    void testUpdateRoom() throws Exception {
        long id = 1L;
        var input = new RoomInput("Room 1", 25, List.of());
        Room updated = Room.builder().id(1l).name("Room 1").build();

        Mockito.when(service.updateRoom(anyLong(), any()))
                .thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/room/{id}", id)
                .with(csrf())
                .content(objectMapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()));
        Mockito.verify(service).updateRoom(anyLong(), any());
    }
}
