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

import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.model.inputs.SharedEquipmentInput;
import com.sme.meeting_planner.services.SharedEquipmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/shared-equipment")
@RequiredArgsConstructor
public class SharedEquipmentController {
    private final SharedEquipmentService service;

    @PostMapping()
    public SharedEquipment createSharedEquipment(@RequestBody SharedEquipmentInput input) {
        return service.createSharedEquipment(input);
    }

    @PutMapping("{id}")
    public SharedEquipment updateSharedEquipment(@RequestBody SharedEquipmentInput input, @PathVariable long id) {
        return service.updateSharedEquipment(id, input);
    }

    @GetMapping("{id}")
    public SharedEquipment getSharedEquipment(@PathVariable long id) {
        return service.getSharedEquipmentById(id);
    }

    @DeleteMapping("{id}")
    public void deleteSharedEquipment(@PathVariable long id) {
        service.deleteSharedEquipment(id);
    }

    @GetMapping()
    public List<SharedEquipment> getAllSharedEquipments(@RequestParam int index, @RequestParam int size) {
        return service.getSharedEquipments(PageRequest.of(index, size));
    }

    @GetMapping("count")
    public long getSharedEquipmentsCount() {
        return service.getSharedEquipmentsCount();
    }
}
