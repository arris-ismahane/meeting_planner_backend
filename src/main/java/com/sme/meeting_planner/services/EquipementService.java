package com.sme.meeting_planner.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.inputs.EquipementInput;
import com.sme.meeting_planner.repositories.EquipementRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class EquipementService {
    private final EquipementRepository repository;
    private final ObjectMapper mapper;
    @Value("${initEquipementfile}")
    private String filePath;

    @PostConstruct
    @SneakyThrows
    @Transactional
    private void init() {
        if (repository.count() == 0) {
            var inputStream = new ClassPathResource(filePath).getInputStream();
            List<Equipement> result = mapper.readValue(inputStream, new TypeReference<List<Equipement>>() {
            });
            repository.saveAll(result);
        }
    }

    public List<Equipement> getEquipements(int index, int size) {
        if (index >= 0 && size > 0) {
            var pageable = PageRequest.of(index, size);
            return repository.findAll(pageable).getContent();
        }
        return repository.findAll();
    }

    public Equipement getEquipementById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ElementNotFound("Element Not Found", "Equipement Not Found"));
    }

    public void deleteEquipement(long id) {
        repository.deleteById(id);
    }

    public Equipement createEquipement(EquipementInput input) {
        return repository.save(input.getEntity());
    }

    public Equipement updateEquipement(long id, EquipementInput input) {
        var rquipement = getEquipementById(id);
        rquipement.setName(input.name());
        return repository.save(rquipement);
    }

    public long getEquipementsCount() {
        return repository.count();
    }

    public List<Equipement> getEquipementsByIds(List<Long> requiredEquipementIds) {
        return repository.findAllById(requiredEquipementIds);
    }

}
