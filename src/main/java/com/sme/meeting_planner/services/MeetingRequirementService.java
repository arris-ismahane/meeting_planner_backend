package com.sme.meeting_planner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.exceptions.DuplicatedElementFound;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.enums.MeetingType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;

import com.sme.meeting_planner.model.inputs.MeetingRequirementInput;
import com.sme.meeting_planner.repositories.MeetingRequirementRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class MeetingRequirementService {
    private final MeetingRequirementRepository repository;
    private final EquipementService equipementService;
    private final ObjectMapper mapper;
    @Value("${initMeetingTypefile}")
    private String filePath;

    @PostConstruct
    @SneakyThrows
    @Transactional
    private void init() {
        if (repository.count() == 0) {
            var inputStream = new ClassPathResource(filePath).getInputStream();
            List<MeetingRequirement> result = mapper.readValue(inputStream,
                    new TypeReference<List<MeetingRequirement>>() {
                    });
            repository.saveAll(result);
        }
    }

    public List<MeetingRequirement> getMeetingRequirements(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public MeetingRequirement getMeetingRequirementById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ElementNotFound("Element Not Found", "MeetingRequirement Not Found"));
    }

    public Optional<MeetingRequirement> getMeetingRquriRequirementByType(MeetingType type) {
        return repository.findOneByType(type);
    }

    public void deleteMeetingRequirement(long id) {
        repository.deleteById(id);
    }

    public MeetingRequirement createMeetingRequirement(MeetingRequirementInput input) {
        if (repository.existsByType(input.type())) {
            throw new DuplicatedElementFound("Duplicated Element Found", "Duplicated Meeting Requirement");
        }
        var equipments = equipementService.getEquipementsByIds(input.requiredEquipementIds());
        var meeting = input.getEntity();
        meeting.setRequiredEquipements(equipments);
        return repository.save(meeting);
    }

    public MeetingRequirement updateMeetingRequirement(long id, MeetingRequirementInput input) {
        var meetingRequirement = getMeetingRequirementById(id);
        var equipments = equipementService.getEquipementsByIds(input.requiredEquipementIds());
        meetingRequirement.setRequiredEquipements(equipments);
        meetingRequirement.setType(input.type());
        return repository.save(meetingRequirement);
    }

    public long getMeetingRequirementsCount() {
        return repository.count();
    }
}
