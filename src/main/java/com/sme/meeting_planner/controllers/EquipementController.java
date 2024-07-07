package com.sme.meeting_planner.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.inputs.EquipementInput;
import com.sme.meeting_planner.services.EquipementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/equipement")
@RequiredArgsConstructor
public class EquipementController {
    private final EquipementService service;

    @PostMapping()
    public Equipement createEquipement(@RequestBody EquipementInput input) {
        return service.createEquipement(input);
    }

    @PutMapping("{id}")
    public Equipement updateEquipement(@RequestBody EquipementInput input, @PathVariable long id) {
        return service.updateEquipement(id, input);
    }

    @GetMapping("{id}")
    public Equipement getEquipement(@PathVariable long id) {
        return service.getEquipementById(id);
    }

    @DeleteMapping("{id}")
    public void deleteEquipement(@PathVariable long id) {
        service.deleteEquipement(id);
    }

    @GetMapping()
    public List<Equipement> getAllEquipements(@RequestParam int index, @RequestParam int size) {
        return service.getEquipements(index, size);
    }

    @GetMapping("count")
    public long getEquipementsCount() {
        return service.getEquipementsCount();
    }
}
