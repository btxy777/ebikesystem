package com.ebike.controller;

import com.ebike.common.Response;
import com.ebike.entity.Vehicle;
import com.ebike.service.FaultReportService;
import com.ebike.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private FaultReportService faultReportService;

    @GetMapping("/list")
    public Response<Map<String, Object>> getVehicleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        vehicles.forEach(this::convertStatus);
        int total = vehicles.size();
        Map<String, Object> result = new HashMap<>();
        if (total == 0) {
            result.put("data", vehicles);
            result.put("total", 0);
            result.put("page", page);
            result.put("size", size);
            return Response.success(result);
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            start = 0;
        }
        List<Vehicle> pageData = vehicles.subList(start, end);
        result.put("data", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Response.success(result);
    }

    @GetMapping("/available")
    public Response<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
        vehicles.forEach(this::convertStatus);
        return Response.success(vehicles);
    }

    @GetMapping("/detail")
    public Response<Map<String, Object>> getVehicleDetail(@RequestParam Long id) {
        if (id == null) {
            return Response.error("车辆ID不能为空");
        }
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null) {
            convertStatus(vehicle);
            Map<String, Object> result = new HashMap<>();
            result.put("id", vehicle.getId());
            result.put("vehicleCode", vehicle.getVehicleCode());
            result.put("vehicleType", vehicle.getVehicleType());
            result.put("status", vehicle.getStatus());
            result.put("statusName", vehicle.getStatusName());
            result.put("batteryLevel", vehicle.getBatteryLevel());
            result.put("currentStationId", vehicle.getCurrentStationId());
            result.put("currentStation", vehicle.getCurrentStation());
            result.put("createTime", vehicle.getCreateTime());
            result.put("lastMaintenanceTime", vehicle.getLastMaintenanceTime());
            return Response.success(result);
        } else {
            return Response.error("车辆不存在");
        }
    }

    @GetMapping("/maintenance-stats")
    public Response<Map<String, Object>> getMaintenanceStats() {
        Map<String, Object> stats = new HashMap<>();
        int faultCount = vehicleService.getFaultVehicleCount();
        int lowBatteryCount = vehicleService.getLowBatteryVehicleCount();
        stats.put("faultCount", faultCount);
        stats.put("lowBatteryCount", lowBatteryCount);
        stats.put("pendingCount", faultCount + lowBatteryCount);
        return Response.success(stats);
    }

    @PostMapping("/add")
    public Response<String> addVehicle(@RequestBody Vehicle vehicle) {
        if (vehicle.getVehicleCode() == null || vehicle.getVehicleCode().trim().isEmpty()) {
            return Response.error("车辆编码不能为空");
        }
        if (vehicle.getVehicleType() == null || vehicle.getVehicleType().trim().isEmpty()) {
            return Response.error("车辆类型不能为空");
        }
        vehicle.setStatus(1);
        vehicle.setBatteryLevel(vehicle.getBatteryLevel() != null ? vehicle.getBatteryLevel() : 100);
        vehicle.setCreateTime(new Date());
        boolean success = vehicleService.addVehicle(vehicle);
        if (success) {
            return Response.success("新增成功");
        }
        return Response.error("新增失败");
    }

    @PutMapping("/update")
    public Response<String> updateVehicle(@RequestBody Vehicle vehicle) {
        if (vehicle.getId() == null) {
            return Response.error("车辆ID不能为空");
        }
        Vehicle existing = vehicleService.getVehicleById(vehicle.getId());
        if (existing == null) {
            return Response.error("车辆不存在");
        }
        existing.setVehicleCode(vehicle.getVehicleCode());
        existing.setVehicleType(vehicle.getVehicleType());
        existing.setBatteryLevel(vehicle.getBatteryLevel());
        existing.setStatus(vehicle.getStatus());
        if (vehicle.getBatteryLevel() != null && vehicle.getBatteryLevel() < 20) {
            existing.setStatus(4);
        }
        boolean success = vehicleService.updateVehicle(existing);
        if (success) {
            return Response.success("更新成功");
        }
        return Response.error("更新失败");
    }

    private void convertStatus(Vehicle vehicle) {
        if (vehicle.getStatus() == null) return;
        String statusName;
        switch (vehicle.getStatus()) {
            case 0: statusName = "离线"; break;
            case 1: statusName = "空闲"; break;
            case 2: statusName = "使用中"; break;
            case 3: statusName = "故障"; break;
            case 4: statusName = "维修中"; break;
            default: statusName = "未知";
        }
        vehicle.setStatusName(statusName);
    }
}
