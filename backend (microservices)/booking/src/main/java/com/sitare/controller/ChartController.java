package com.sitare.controller;

import com.sitare.modal.Booking;
import com.sitare.payload.dto.SalonDTO;

import com.sitare.service.BookingService;
import com.sitare.service.clients.SalonGrpcClient;
import com.sitare.service.impl.BookingChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings/chart")
public class ChartController {

    private final BookingChartService bookingChartService;
    private final BookingService bookingService;
    private final SalonGrpcClient salonService;

    @GetMapping("/earnings")
    public ResponseEntity<List<Map<String, Object>>> getEarningsChartData(
            @RequestHeader("Authorization") String jwt) throws Exception {

//        UserDTO user = userService.getUserFromJwtToken(jwt).getBody();
        SalonDTO salon = salonService.getSalonByOwner(jwt);
        List<Booking> bookings=bookingService.getBookingsBySalon(salon.getId());

        // Generate chart data
        List<Map<String, Object>> chartData = bookingChartService
                .generateEarningsChartData(bookings);
        return ResponseEntity.ok(chartData);

    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Map<String, Object>>> getBookingsChartData(
            @RequestHeader("Authorization") String jwt) throws Exception {
        SalonDTO salon = salonService.getSalonByOwner(jwt);
        List<Booking> bookings=bookingService.getBookingsBySalon(salon.getId());
        // Generate chart data
        List<Map<String, Object>> chartData = bookingChartService.generateBookingCountChartData(bookings);
        return ResponseEntity.ok(chartData);
    }
}
