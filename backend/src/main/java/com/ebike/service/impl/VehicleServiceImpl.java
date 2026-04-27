package com.ebike.service.impl;

import com.ebike.entity.Vehicle;
import com.ebike.mapper.VehicleMapper;
import com.ebike.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleMapper.selectAll();
    }

    @Override
    public List<Vehicle> getAvailableVehicles() {
        return vehicleMapper.selectByStatus(1); // 1表示空闲状态
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        return vehicleMapper.selectById(id);
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) {
        return vehicleMapper.insert(vehicle) > 0;
    }

    @Override
    public boolean updateVehicleStatus(Long vehicleId, Integer status) {
        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle != null) {
            vehicle.setStatus(status);
            int result = vehicleMapper.update(vehicle);
            return result > 0;
        }
        return false;
    }

    @Override
    public boolean updateVehicleStation(Long vehicleId, Long stationId) {
        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle != null) {
            vehicle.setCurrentStationId(stationId);
            int result = vehicleMapper.update(vehicle);
            return result > 0;
        }
        return false;
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleMapper.update(vehicle) > 0;
    }

    @Override
    public long getVehicleCount() {
        return vehicleMapper.selectCount();
    }

    @Override
    public int getFaultVehicleCount() {
        return vehicleMapper.selectFaultCount();
    }

    @Override
    public int getLowBatteryVehicleCount() {
        return vehicleMapper.selectLowBatteryCount();
    }
}
