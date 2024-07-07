package com.sme.meeting_planner.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sme.meeting_planner.BasicControllerTest;
import com.sme.meeting_planner.model.Booking;
import com.sme.meeting_planner.model.inputs.MeetingInput;
import com.sme.meeting_planner.services.BookingService;

@WebMvcTest(BookingController.class)
public class BookingControllerTest extends BasicControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private BookingService bookingService;

        @Test
        void testCreateBooking() throws Exception {
                var input = new MeetingInput("R1", 0, 0, 1, 5);
                var expectedBooking = Booking.builder().id(1l).name("R1").build();
                when(bookingService.makeReservation(any())).thenReturn(expectedBooking);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/admin/booking")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(expectedBooking)));
        }

        @Test
        void testDeleteBooking() throws Exception {
                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/admin/booking/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void testGetAllBookingPageable() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.get("/admin/booking")
                                                .with(csrf())
                                                .param("index", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk());

                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                verify(bookingService).getBookings(pageableCaptor.capture());

                PageRequest pageable = (PageRequest) pageableCaptor.getValue();
                assertEquals(0, pageable.getPageNumber());
                assertEquals(10, pageable.getPageSize());
        }

        @Test
        void testGetBooking() throws Exception {
                long id = 1L;
                var expectedBooking = Booking.builder().id(1l).name("R1").build();

                Mockito.when(bookingService.getBookingById(anyLong())).thenReturn(expectedBooking);
                mockMvc.perform(MockMvcRequestBuilders.get("/admin/booking/1")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id))
                                .andExpect(content().json(objectMapper.writeValueAsString(expectedBooking)));
        }

        @Test
        void testGetBookingsByRange() throws Exception {
                long startDate = 1720134000000L;
                long endDate = 1720220400000L;
                List<Booking> bookings = List.of(
                                Booking.builder().id(1l).name("R1").build(),
                                Booking.builder().id(2l).name("R2").build());
                Mockito.when(bookingService.getBookingsByRange(startDate, endDate)).thenReturn(bookings);
                mockMvc.perform(MockMvcRequestBuilders.get("/admin/booking/date-range")
                                .param("startDate", String.valueOf(startDate))
                                .param("endDate", String.valueOf(endDate)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/json"))
                                .andExpect(jsonPath("$.length()").value(bookings.size()));

                Mockito.verify(bookingService).getBookingsByRange(anyLong(), anyLong());
        }

        @Test
        void testGetBookingsCount() throws Exception {
                long count = 5;
                Mockito.when(bookingService.getBookingsCount()).thenReturn(count);

                mockMvc.perform(MockMvcRequestBuilders.get("/admin/booking/count"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/json"))
                                .andExpect(jsonPath("$").value(count));

                Mockito.verify(bookingService).getBookingsCount();
        }
}
