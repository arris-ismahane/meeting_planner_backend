package com.sme.meeting_planner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.exceptions.DuplicatedElementFound;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.SharedEquipment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;

import com.sme.meeting_planner.model.inputs.SharedEquipmentInput;
import com.sme.meeting_planner.repositories.SharedEquipmentRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class SharedEquipmentService {
    private final SharedEquipmentRepository repository;
    private final EquipementService equipementService;
    private final ObjectMapper mapper;
    @Value("${initSharedEquipementfile}")
    private String filePath;

    @PostConstruct
    @SneakyThrows
    @Transactional
    private void init() {
        if (repository.count() == 0) {
            var inputStream = new ClassPathResource(filePath).getInputStream();
            List<SharedEquipment> result = mapper.readValue(inputStream, new TypeReference<List<SharedEquipment>>() {
            });
            repository.saveAll(result);
        }
    }

    public List<SharedEquipment> getSharedEquipments(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public SharedEquipment getSharedEquipmentById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ElementNotFound("Element Not Found", "SharedEquipment Not Found"));
    }

    public void deleteSharedEquipment(long id) {
        repository.deleteById(id);
    }

    public SharedEquipment createSharedEquipment(SharedEquipmentInput input) {
        if (repository.existsByEquipmentId(input.equipmentId())) {
            throw new DuplicatedElementFound("Duplicated Element Found", "Duplicated Shared Equipment");
        }
        var equipment = equipementService.getEquipementById(input.equipmentId());
        var shared = input.getEntity();
        shared.setEquipment(equipment);
        return repository.save(shared);
    }

    public SharedEquipment updateSharedEquipment(long id, SharedEquipmentInput input) {
        var equipment = equipementService.getEquipementById(input.equipmentId());
        var sharedEquipment = getSharedEquipmentById(id);
        sharedEquipment.setEquipment(equipment);
        sharedEquipment.setTotal(input.total());
        return repository.save(sharedEquipment);
    }

    public long getSharedEquipmentsCount() {
        return repository.count();
    }

    public Optional<SharedEquipment> book(long equipmentId, long startDate, long endDate) {
        return repository.findAvailableEquipment(equipmentId, startDate, endDate);
    }
}
