package com.sme.meeting_planner.services;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.inputs.RoomInput;
import com.sme.meeting_planner.repositories.RoomRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository repository;
    private final EquipementService equipementService;
    @Value("${maxRoomUsableCapacity}")
    private double maxCapacity;
    @Value("${nbCleaningHours}")
    private int nbCleaningHours;
    private final ObjectMapper mapper;
    @Value("${initRoomfile}")
    private String filePath;

    @PostConstruct
    @SneakyThrows
    @Transactional
    private void init() {
        if (repository.count() == 0) {
            var inputStream = new ClassPathResource(filePath).getInputStream();
            List<Room> result = mapper.readValue(inputStream, new TypeReference<List<Room>>() {
            });
            repository.saveAll(result);
        }
    }

    public List<Room> getRooms(int index, int size) {
        if (index >= 0 && size > 0) {
            var pageable = PageRequest.of(index, size);
            return repository.findAll(pageable).getContent();
        }
        return repository.findAll();
    }

    public Room getRoomById(long id) {
        return repository.findById(id).orElseThrow(() -> new ElementNotFound("Element Not Found", "Room Not Found"));
    }

    public void deleteRoom(long id) {
        repository.deleteById(id);
    }

    public Room createRoom(RoomInput input) {
        var equipments = equipementService.getEquipementsByIds(input.initialEquipmentIds());
        var room = input.getEntity();
        room.setInitialEquipments(equipments);
        return repository.save(room);
    }

    public Room updateRoom(long id, RoomInput input) {
        var equipments = equipementService.getEquipementsByIds(input.initialEquipmentIds());
        var room = getRoomById(id);
        room.setName(input.name());
        room.setCapacity(input.capacity());
        room.setInitialEquipments(equipments);
        return repository.save(room);
    }

    public long getRoomsCount() {
        return repository.count();
    }

    public List<Room> getAvailableRooms(int nbParticipants, long startDate, long endDate) {
        long gap = nbCleaningHours * 60 * 60 * 1000;
        return repository.findRoomsNotBookedInRange(nbParticipants, startDate, endDate, gap, maxCapacity);
    }

}
