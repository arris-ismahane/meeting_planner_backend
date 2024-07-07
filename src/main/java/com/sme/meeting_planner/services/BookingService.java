package com.sme.meeting_planner.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import com.sme.meeting_planner.model.Room;
import com.sme.meeting_planner.model.SharedEquipment;
import com.sme.meeting_planner.exceptions.ElementNotFound;
import com.sme.meeting_planner.exceptions.ElementOutOfCapacity;
import com.sme.meeting_planner.exceptions.NoRoomAvailableException;
import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.Equipement;
import com.sme.meeting_planner.model.MeetingRequirement;
import com.sme.meeting_planner.model.inputs.MeetingInput;
import com.sme.meeting_planner.repositories.BookingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final RoomService roomService;
    private final MeetingRequirementService meetingRequirementService;
    private final SharedEquipmentService sharedEquipmentService;

    public List<Booking> getBookings(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public Booking getBookingById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ElementNotFound("Element Not Found", "Booking is not found"));
    }

    public void deleteBooking(long id) {
        repository.deleteById(id);
    }

    public long getBookingsCount() {
        return repository.count();
    }

    @Transactional
    public Booking makeReservation(MeetingInput meetingInput) {
        List<Room> rooms = roomService.getAvailableRooms(meetingInput.nbParticipants(), meetingInput.startDate(),
                meetingInput.endDate());
        if (rooms.isEmpty()) {
            throw new NoRoomAvailableException("No Room Available", "No room available for this booking configuration");
        }
        MeetingRequirement optMeetReq = meetingRequirementService
                .getMeetingRequirementById(meetingInput.typeId());

        List<Equipement> equipments = optMeetReq.getRequiredEquipements();
        List<SharedEquipment> bookedEquipments = new ArrayList<>();
        Room bookedRoom = roomWithMostEquipments(rooms, equipments);
        List<Equipement> missingEquipments = equipments.stream()
                .filter(equip -> !bookedRoom.getInitialEquipments().contains(equip)).toList();
        for (var equipment : missingEquipments) {
            Optional<SharedEquipment> booked = sharedEquipmentService.book(equipment.getId(),
                    meetingInput.startDate(), meetingInput.endDate());
            if (booked.isEmpty()) {
                throw new ElementOutOfCapacity("Element Out Of Capacity", "Equipement Fully Booked");
            }
            bookedEquipments.add(booked.get());
        }

        var booking = new Booking(meetingInput.name(), meetingInput.startDate(), meetingInput.endDate(),
                optMeetReq,
                meetingInput.nbParticipants(), bookedRoom, bookedEquipments);
        return repository.save(booking);
    }

    private Room roomWithMostEquipments(List<Room> rooms, List<Equipement> equipments) {
        int maxIndex = -1;
        int maxValue = -1;

        for (int i = 0; i < rooms.size(); i++) {
            int count = 0;
            for (var equip : equipments) {
                if (rooms.get(i).getInitialEquipments().stream()
                        .map(Equipement::getId)
                        .collect(Collectors.toList()).contains(equip.getId())) {
                    count++;
                }
            }

            if (count > maxValue
                    || ((count == maxValue) && (rooms.get(i).getCapacity() < rooms.get(maxIndex).getCapacity()))) {
                maxValue = count;
                maxIndex = i;
            }
        }

        return rooms.get(maxIndex);
    }

    public List<Booking> getBookingsByRange(long startDate, long endDate) {
        return repository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);
    }
}
