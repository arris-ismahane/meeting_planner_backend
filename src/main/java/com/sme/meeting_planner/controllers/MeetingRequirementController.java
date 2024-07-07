package com.sme.meeting_planner.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.inputs.MeetingRequirementInput;
import com.sme.meeting_planner.services.MeetingRequirementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/meeting-requirement")
@RequiredArgsConstructor
public class MeetingRequirementController {
    private final MeetingRequirementService service;

    @PostMapping()
    public MeetingRequirement createMeetingRequirement(@RequestBody MeetingRequirementInput input) {
        return service.createMeetingRequirement(input);
    }

    @PutMapping("{id}")
    public MeetingRequirement updateMeetingRequirement(@RequestBody MeetingRequirementInput input,
            @PathVariable long id) {
        return service.updateMeetingRequirement(id, input);
    }

    @GetMapping("{id}")
    public MeetingRequirement getMeetingRequirement(@PathVariable long id) {
        return service.getMeetingRequirementById(id);
    }

    @DeleteMapping("{id}")
    public void deleteMeetingRequirement(@PathVariable long id) {
        service.deleteMeetingRequirement(id);
    }

    @GetMapping()
    public List<MeetingRequirement> getAllMeetingRequirements(@RequestParam int index, @RequestParam int size) {
        return service.getMeetingRequirements(PageRequest.of(index, size));
    }

    @GetMapping("count")
    public long getMeetingRequirementsCount() {
        return service.getMeetingRequirementsCount();
    }

}
