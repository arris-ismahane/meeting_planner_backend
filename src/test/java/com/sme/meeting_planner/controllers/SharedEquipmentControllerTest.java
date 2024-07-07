package com.sme.meeting_planner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.BasicControllerTest;
import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.model.inputs.SharedEquipmentInput;
import com.sme.meeting_planner.services.SharedEquipmentService;
import java.util.List;

@WebMvcTest(SharedEquipmentController.class)
public class SharedEquipmentControllerTest extends BasicControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private SharedEquipmentService service;

        @Test
        void testCreateSharedEquipment() throws Exception {
                var input = new SharedEquipmentInput(1, 4);
                var expected = SharedEquipment.builder().id(1l).total(4).build();
                when(service.createSharedEquipment(any())).thenReturn(expected);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/admin/shared-equipment")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
        }

        @Test
        void testDeleteSharedEquipment() throws Exception {
                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/admin/shared-equipment/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void testGetAllSharedEquipments() throws Exception {
                List<SharedEquipment> equipements = List.of(
                                SharedEquipment.builder().id(1L).total(5).build(),
                                SharedEquipment.builder().id(2L).total(5).build());
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                when(service.getSharedEquipments(pageableCaptor.capture())).thenReturn(equipements);
                mockMvc.perform(
                                MockMvcRequestBuilders.get("/admin/shared-equipment")
                                                .with(csrf())
                                                .param("index", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(equipements.size()));
                verify(service).getSharedEquipments(pageableCaptor.capture());

        }

        @Test
        void testGetSharedEquipment() throws Exception {
                long id = 1L;
                var expected = SharedEquipment.builder().id(1l).total(4).build();

                Mockito.when(service.getSharedEquipmentById(anyLong())).thenReturn(expected);
                mockMvc.perform(MockMvcRequestBuilders.get("/admin/shared-equipment/1")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id))
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
        }

        @Test
        void testGetSharedEquipmentsCount() throws Exception {
                long count = 4;
                Mockito.when(service.getSharedEquipmentsCount()).thenReturn(count);

                mockMvc.perform(MockMvcRequestBuilders.get("/admin/shared-equipment/count"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/json"))
                                .andExpect(jsonPath("$").value(count));

                Mockito.verify(service).getSharedEquipmentsCount();
        }

        @Test
        void testUpdateSharedEquipment() throws Exception {
                long id = 1L;
                SharedEquipmentInput input = new SharedEquipmentInput(1, 2);
                SharedEquipment updated = SharedEquipment.builder().id(id).total(2).build();

                Mockito.when(service.updateSharedEquipment(anyLong(), any()))
                                .thenReturn(updated);

                mockMvc.perform(MockMvcRequestBuilders.put("/admin/shared-equipment/{id}", id)
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.total").value(updated.getTotal()));
                Mockito.verify(service).updateSharedEquipment(anyLong(), any());
        }
}
