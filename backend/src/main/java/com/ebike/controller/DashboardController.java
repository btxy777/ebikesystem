package com.ebike.controller;

import com.ebike.common.Response;
import com.ebike.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private RideOrderService rideOrderService;

    @Autowired
    private FaultReportService faultReportService;

    @GetMapping("/stats")
    public Response<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.getUserCount());
        stats.put("vehicleCount", vehicleService.getVehicleCount());
        stats.put("orderCount", rideOrderService.getOrderCount());
        stats.put("faultCount", faultReportService.getFaultCount());
        return Response.success(stats);
    }
}
