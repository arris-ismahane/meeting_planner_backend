package com.sme.meeting_planner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.enums.MeetingType;
import com.sme.meeting_planner.model.inputs.MeetingRequirementInput;
import com.sme.meeting_planner.services.MeetingRequirementService;
import java.util.List;

@WebMvcTest(MeetingRequirementController.class)
public class MeetingRequirementControllerTest extends BasicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingRequirementService service;

    @Test
    void testCreateMeetingRequirement() throws Exception {
        var input = new MeetingRequirementInput(MeetingType.RS, List.of());
        var expected = MeetingRequirement.builder().id(1l).type(MeetingType.RS).build();
        when(service.createMeetingRequirement(any())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/admin/meeting-requirement")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testDeleteMeetingRequirement() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/admin/meeting-requirement/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllMeetingRequirements() throws Exception {
        List<MeetingRequirement> equipements = List.of(
                MeetingRequirement.builder().id(1L).type(MeetingType.RC).build(),
                MeetingRequirement.builder().id(2L).type(MeetingType.VC).build());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(service.getMeetingRequirements(pageableCaptor.capture())).thenReturn(equipements);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/meeting-requirement")
                        .with(csrf())
                        .param("index", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equipements.size()));
        verify(service).getMeetingRequirements(any());
    }

    @Test
    void testGetMeetingRequirement() throws Exception {
        long id = 1L;
        var expected = MeetingRequirement.builder().id(1L).type(MeetingType.RC).build();

        Mockito.when(service.getMeetingRequirementById(anyLong())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/meeting-requirement/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetMeetingRequirementsCount() throws Exception {
        long count = 4;
        Mockito.when(service.getMeetingRequirementsCount()).thenReturn(count);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/meeting-requirement/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").value(count));

        Mockito.verify(service).getMeetingRequirementsCount();

    }

    @Test
    void testUpdateMeetingRequirement() throws Exception {
        long id = 1L;
        MeetingRequirementInput input = new MeetingRequirementInput(MeetingType.SPEC, List.of());
        MeetingRequirement updated = MeetingRequirement.builder().id(1L).type(MeetingType.RS).build();

        Mockito.when(service.updateMeetingRequirement(anyLong(), any()))
                .thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/meeting-requirement/{id}", id)
                .with(csrf())
                .content(objectMapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(updated.getType().name()));
        Mockito.verify(service).updateMeetingRequirement(anyLong(), any());
    }
}
