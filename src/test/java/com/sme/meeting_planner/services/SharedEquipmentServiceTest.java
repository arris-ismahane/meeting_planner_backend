package com.sme.meeting_planner.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.sme.meeting_planner.exceptions.DuplicatedElementFound;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.model.inputs.SharedEquipmentInput;
import com.sme.meeting_planner.repositories.SharedEquipmentRepository;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SharedEquipmentServiceTest {
    @InjectMocks
    private SharedEquipmentService service;
    @Mock
    private SharedEquipmentRepository repository;
    @Mock
    private EquipementService equipementService;

    private SharedEquipment expectedSharedEquipment;
    private SharedEquipmentInput sharedEquipmentInput;
    private Equipement equipement;
    private long id = 1l;

    @BeforeEach
    void setUp() {
        equipement = Equipement.builder().id(1l).name("Screen").build();
        expectedSharedEquipment = SharedEquipment.builder().id(id).equipment(equipement).total(4).build();
        sharedEquipmentInput = new SharedEquipmentInput(1, 4);
    }

    @Test
    void testCreateSharedEquipment() throws Exception {
        when(repository.save(any(SharedEquipment.class))).thenReturn(expectedSharedEquipment);
        when(repository.existsByEquipmentId(anyLong())).thenReturn(false);

        SharedEquipment actual = service.createSharedEquipment(sharedEquipmentInput);

        Assertions.assertThat(actual).isEqualTo(expectedSharedEquipment);
        verify(repository, times(1)).save(any());
        verify(repository, times(1)).existsByEquipmentId(anyLong());
        verify(equipementService, times(1)).getEquipementById(anyLong());

        verifyNoMoreInteractions(repository);

    }

    @Test
    void testCreateSharedEquipment_should_throw_exception() throws Exception {
        when(repository.existsByEquipmentId(anyLong())).thenReturn(true);

        assertThrows(DuplicatedElementFound.class, () -> service.createSharedEquipment(sharedEquipmentInput));

        verify(repository, times(1)).existsByEquipmentId(anyLong());
        verify(equipementService, times(0)).getEquipementById(anyLong());

        verify(repository, times(0)).save(any());

    }

    @Test
    void testDeleteSharedEquipment() {
        doNothing().when(repository).deleteById(anyLong());

        service.deleteSharedEquipment(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetSharedEquipmentById_should_throw_exception() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ElementNotFound.class, () -> service.getSharedEquipmentById(1l));
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void testGetSharedEquipmentById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expectedSharedEquipment));

        final var result = service.getSharedEquipmentById(anyLong());
        Assertions.assertThat(result).isEqualTo(expectedSharedEquipment);
        verify(repository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetSharedEquipments() {
        SharedEquipment sharedEquipment = SharedEquipment.builder().id(2l).total(6).build();
        List<SharedEquipment> list = List.of(expectedSharedEquipment, sharedEquipment);

        Page<SharedEquipment> page = new PageImpl<>(list);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(repository.findAll(pageableCaptor.capture())).thenReturn(page);
        var result = service.getSharedEquipments(any());

        assertEquals(list.size(), result.size());
        assertTrue(result.contains(sharedEquipment));
    }

    @Test
    void testUpdateSharedEquipment() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expectedSharedEquipment));
        when(repository.save(any(SharedEquipment.class))).thenReturn(expectedSharedEquipment);

        SharedEquipment updatedEquipment = service.updateSharedEquipment(id, sharedEquipmentInput);
        assertEquals(updatedEquipment.getId(), expectedSharedEquipment.getId());
        verify(equipementService, times(1)).getEquipementById(anyLong());
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).save(any());
    }

}
