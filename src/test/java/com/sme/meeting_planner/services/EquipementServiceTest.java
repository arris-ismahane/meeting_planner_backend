package com.sme.meeting_planner.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.inputs.EquipementInput;
import com.sme.meeting_planner.repositories.EquipementRepository;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)

public class EquipementServiceTest {
    @InjectMocks
    private EquipementService service;

    @Mock
    private EquipementRepository repository;

    private Equipement expected;
    private EquipementInput input;
    private Equipement equipement;
    private long id = 1l;

    @BeforeEach
    void setUp() {
        equipement = Equipement.builder().id(2l).name("Board").build();
        input = new EquipementInput("Screen");
        expected = Equipement
                .builder()
                .id(id)
                .name("Screen")
                .build();
    }

    @Test
    void testCreateEquipement() {
        when(repository.save(any(Equipement.class))).thenReturn(expected);

        Equipement actual = service.createEquipement(input);

        Assertions.assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
        verify(repository).save(any());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void testDeleteEquipement() {
        doNothing().when(repository).deleteById(anyLong());

        service.deleteEquipement(anyLong());
        verify(repository).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetEquipementById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));

        final var result = service.getEquipementById(anyLong());
        Assertions.assertThat(result).isEqualTo(expected);
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetEquipementById_should_throw_exception() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ElementNotFound.class, () -> service.getEquipementById(1l));
        verify(repository).findById(anyLong());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void testGetEquipements() {

        List<Equipement> list = List.of(expected, equipement);

        when(repository.findAll()).thenReturn(list);
        List<Equipement> result = service.getEquipements(-1, -1);

        assertEquals(list.size(), result.size());
        assertTrue(result.contains(equipement));
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testUpdateEquipement() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(repository.save(any(Equipement.class))).thenReturn(expected);

        Equipement updated = service.updateEquipement(id, input);
        assertEquals(updated.getId(), expected.getId());
        verify(repository).findById(anyLong());
        verify(repository).save(any());
    }
}
