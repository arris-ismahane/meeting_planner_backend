package com.sme.meeting_planner.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.sme.meeting_planner.exceptions.DuplicatedElementFound;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.enums.MeetingType;
import com.sme.meeting_planner.model.inputs.MeetingRequirementInput;
import com.sme.meeting_planner.repositories.MeetingRequirementRepository;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)

public class MeetingRequirementServiceTest {
    @InjectMocks
    private MeetingRequirementService service;
    @Mock
    private EquipementService equipementService;
    @Mock
    private MeetingRequirementRepository repository;

    private MeetingRequirement expected;
    private MeetingRequirementInput input;
    private Equipement equipement;
    private long id = 1l;

    @BeforeEach
    void setUp() {
        equipement = Equipement.builder().id(1l).name("Screen").build();
        input = new MeetingRequirementInput(MeetingType.RS, List.of(1l));
        expected = MeetingRequirement
                .builder()
                .id(id)
                .type(MeetingType.RS)
                .requiredEquipements(List.of(equipement))
                .build();
    }

    @Test
    void testCreateMeetingRequirement() {
        when(repository.save(any(MeetingRequirement.class))).thenReturn(expected);
        when(repository.existsByType(any())).thenReturn(false);
        MeetingRequirement actual = service.createMeetingRequirement(input);

        Assertions.assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
        verify(repository).save(any());
        verify(repository, times(1)).existsByType(any());

        verify(equipementService).getEquipementsByIds(anyList());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void testCreateSharedEquipment_should_throw_exception() throws Exception {
        when(repository.existsByType(any())).thenReturn(true);

        assertThrows(DuplicatedElementFound.class, () -> service.createMeetingRequirement(input));

        verify(repository, times(1)).existsByType(any());
        verify(equipementService, times(0)).getEquipementById(anyLong());

        verify(repository, times(0)).save(any());

    }

    @Test
    void testDeleteMeetingRequirement() {
        doNothing().when(repository).deleteById(anyLong());

        service.deleteMeetingRequirement(anyLong());
        verify(repository).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetMeetingRequirementById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));

        final var result = service.getMeetingRequirementById(anyLong());
        Assertions.assertThat(result).isEqualTo(expected);
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetMeetingRequirementById_should_throw_exception() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ElementNotFound.class, () -> service.getMeetingRequirementById(1l));
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void testGetMeetingRequirements() {
        MeetingRequirement meetingRequirement2 = MeetingRequirement.builder().id(2l).type(MeetingType.VC).build();
        List<MeetingRequirement> list = List.of(expected, meetingRequirement2);

        Page<MeetingRequirement> page = new PageImpl<>(list);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(repository.findAll(pageableCaptor.capture())).thenReturn(page);
        var result = service.getMeetingRequirements(any());

        assertEquals(list.size(), result.size());
        assertTrue(result.contains(expected));
    }

    @Test
    void testUpdateMeetingRequirement() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(repository.save(any(MeetingRequirement.class))).thenReturn(expected);

        MeetingRequirement updated = service.updateMeetingRequirement(id, input);
        assertEquals(updated.getId(), expected.getId());
        verify(equipementService).getEquipementsByIds(anyList());
        verify(repository).findById(anyLong());
        verify(repository).save(any());
    }
}
