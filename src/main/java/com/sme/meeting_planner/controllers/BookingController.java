package com.sme.meeting_planner.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.inputs.MeetingInput;
import com.sme.meeting_planner.services.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping()
    public Booking createBooking(@RequestBody MeetingInput input) {
        return service.makeReservation(input);
    }

    @GetMapping("{id}")
    public Booking getBooking(@PathVariable long id) {
        return service.getBookingById(id);
    }

    @DeleteMapping("{id}")
    public void deleteBooking(@PathVariable long id) {
        service.deleteBooking(id);
    }

    @GetMapping()
    public List<Booking> getAllBookingPageable(@RequestParam int index, @RequestParam int size) {
        return service.getBookings(PageRequest.of(index, size));
    }

    @GetMapping("count")
    public long getBookingsCount() {
        return service.getBookingsCount();
    }

    @GetMapping("date-range")
    public List<Booking> getBookingsByRange(@RequestParam long startDate, @RequestParam long endDate) {
        return service.getBookingsByRange(startDate, endDate);
    }

}
