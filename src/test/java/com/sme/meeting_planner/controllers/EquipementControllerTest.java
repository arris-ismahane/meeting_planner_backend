package com.sme.meeting_planner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.sme.meeting_planner.model.inputs.EquipementInput;
import com.sme.meeting_planner.services.EquipementService;
import java.util.List;

@WebMvcTest(EquipementController.class)
public class EquipementControllerTest extends BasicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EquipementService service;

    @Test
    void testCreateEquipement() throws Exception {
        var input = new EquipementInput("Screen");
        var expected = Equipement.builder().id(1l).name("Screen").build();
        when(service.createEquipement(any())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/admin/equipement")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testDeleteEquipement() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/admin/equipement/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllEquipements() throws Exception {
        List<Equipement> equipements = List.of(
                Equipement.builder().id(1L).name("Screen").build(),
                Equipement.builder().id(2L).name("Board").build());

        when(service.getEquipements(anyInt(), anyInt())).thenReturn(equipements);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/equipement")
                        .with(csrf())
                        .param("index", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equipements.size()));
        verify(service).getEquipements(anyInt(), anyInt());

    }

    @Test
    void testGetEquipement() throws Exception {
        long id = 1L;
        var expected = Equipement.builder().id(1l).name("Screen").build();

        Mockito.when(service.getEquipementById(anyLong())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/equipement/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetEquipementsCount() throws Exception {
        long count = 4;
        Mockito.when(service.getEquipementsCount()).thenReturn(count);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/equipement/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").value(count));

        Mockito.verify(service).getEquipementsCount();
    }

    @Test
    void testUpdateEquipement() throws Exception {
        long id = 1L;
        EquipementInput input = new EquipementInput("Web Cam");
        Equipement updated = Equipement.builder().id(id).name("Webcam").build();

        Mockito.when(service.updateEquipement(anyLong(), any()))
                .thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/equipement/{id}", id)
                .with(csrf())
                .content(objectMapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()));
        Mockito.verify(service).updateEquipement(anyLong(), any());
    }
}
