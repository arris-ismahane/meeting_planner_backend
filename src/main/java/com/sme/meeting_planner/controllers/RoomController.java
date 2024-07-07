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

import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.inputs.RoomInput;
import com.sme.meeting_planner.services.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService service;

    @PostMapping()
    public Room createRoom(@RequestBody RoomInput input) {
        return service.createRoom(input);
    }

    @PutMapping("{id}")
    public Room updateRoom(@RequestBody RoomInput input, @PathVariable long id) {
        return service.updateRoom(id, input);
    }

    @GetMapping("{id}")
    public Room getRoom(@PathVariable long id) {
        return service.getRoomById(id);
    }

    @DeleteMapping("{id}")
    public void deleteRoom(@PathVariable long id) {
        service.deleteRoom(id);
    }

    @GetMapping()
    public List<Room> getAllRooms(@RequestParam int index, @RequestParam int size) {
        return service.getRooms(index, size);
    }

    @GetMapping("count")
    public long getRoomsCount() {
        return service.getRoomsCount();
    }
}
