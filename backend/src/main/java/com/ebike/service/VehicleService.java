package com.ebike.service;

import com.ebike.entity.Vehicle;
import java.util.List;

public interface VehicleService {
    List<Vehicle> getAllVehicles();
    List<Vehicle> getAvailableVehicles();
    Vehicle getVehicleById(Long id);
    boolean addVehicle(Vehicle vehicle);
    boolean updateVehicleStatus(Long vehicleId, Integer status);
    boolean updateVehicleStation(Long vehicleId, Long stationId);
    boolean updateVehicle(Vehicle vehicle);
    long getVehicleCount();
    int getFaultVehicleCount();
    int getLowBatteryVehicleCount();
}
