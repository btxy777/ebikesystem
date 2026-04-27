package com.ebike.service.impl;

import com.ebike.entity.RideOrder;
import com.ebike.entity.Station;
import com.ebike.entity.Vehicle;
import com.ebike.mapper.RideOrderMapper;
import com.ebike.mapper.StationMapper;
import com.ebike.mapper.VehicleMapper;
import com.ebike.service.RideOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class RideOrderServiceImpl implements RideOrderService {

    @Autowired
    private RideOrderMapper rideOrderMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    @Autowired
    private StationMapper stationMapper;

    private static final int MIN_BATTERY_LEVEL = 20;
    private static final int BATTERY_DECREASE_RATE = 1;

    @Override
    public RideOrder startRide(Long userId, Long vehicleId, Long stationId) {
        RideOrder activeOrder = rideOrderMapper.selectActiveOrderByUserId(userId);
        if (activeOrder != null) {
            return null;
        }

        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle == null) {
            return null;
        }
        if (vehicle.getStatus() == null || vehicle.getStatus() != 1) {
            return null;
        }
        if (vehicle.getBatteryLevel() == null || vehicle.getBatteryLevel() < MIN_BATTERY_LEVEL) {
            return null;
        }

        RideOrder rideOrder = new RideOrder();
        rideOrder.setUserId(userId);
        rideOrder.setVehicleId(vehicleId);
        rideOrder.setStartStationId(stationId);
        rideOrder.setStartTime(new Date());
        rideOrder.setStatus(0);
        rideOrder.setCreateTime(new Date());

        int result = rideOrderMapper.insert(rideOrder);
        if (result > 0) {
            vehicleMapper.updateStatusAndStation(vehicleId, 2, stationId);
            return rideOrderMapper.selectById(rideOrder.getId());
        }
        return null;
    }

    @Override
    public RideOrder startRideWithMessage(Long userId, Long vehicleId, Long stationId) {
        RideOrder activeOrder = rideOrderMapper.selectActiveOrderByUserId(userId);
        if (activeOrder != null) {
            return null;
        }

        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle == null) {
            return null;
        }
        if (vehicle.getStatus() == null || vehicle.getStatus() != 1) {
            return null;
        }
        if (vehicle.getBatteryLevel() == null || vehicle.getBatteryLevel() < MIN_BATTERY_LEVEL) {
            return null;
        }

        RideOrder rideOrder = new RideOrder();
        rideOrder.setUserId(userId);
        rideOrder.setVehicleId(vehicleId);
        rideOrder.setStartStationId(stationId);
        rideOrder.setStartTime(new Date());
        rideOrder.setStatus(0);
        rideOrder.setCreateTime(new Date());

        int result = rideOrderMapper.insert(rideOrder);
        if (result > 0) {
            vehicleMapper.updateStatusAndStation(vehicleId, 2, stationId);
            return rideOrderMapper.selectById(rideOrder.getId());
        }
        return null;
    }

    @Override
    public RideOrder endRide(Long orderId, Long endStationId) {
        RideOrder rideOrder = rideOrderMapper.selectById(orderId);
        if (rideOrder == null || rideOrder.getStatus() != 0) {
            return null;
        }

        Date endTime = new Date();
        long durationMs = endTime.getTime() - rideOrder.getStartTime().getTime();
        int durationMinutes = (int) (durationMs / (1000 * 60));
        if (durationMinutes < 1) {
            durationMinutes = 1;
        }

        Station randomStation = stationMapper.selectRandomStation();
        if (randomStation == null) {
            return null;
        }

        Vehicle vehicle = vehicleMapper.selectById(rideOrder.getVehicleId());
        if (vehicle == null) {
            return null;
        }

        int batteryConsumption = durationMinutes * BATTERY_DECREASE_RATE;
        int newBatteryLevel = vehicle.getBatteryLevel() - batteryConsumption;
        if (newBatteryLevel < 0) {
            newBatteryLevel = 0;
        }

        rideOrder.setEndTime(endTime);
        rideOrder.setEndStationId(randomStation.getId());
        rideOrder.setDuration(durationMinutes);
        rideOrder.setStatus(1);

        int result = rideOrderMapper.update(rideOrder);
        if (result > 0) {
            vehicleMapper.updateStatusAndStation(vehicle.getId(), 1, randomStation.getId());
            vehicleMapper.updateBatteryLevel(vehicle.getId(), newBatteryLevel);
            return rideOrderMapper.selectById(orderId);
        }
        return null;
    }

    @Override
    public List<RideOrder> getAllOrders() {
        return rideOrderMapper.selectAll();
    }

    @Override
    public List<RideOrder> getOrdersByUserId(Long userId) {
        return rideOrderMapper.selectByUserId(userId);
    }

    @Override
    public RideOrder getOrderById(Long orderId) {
        return rideOrderMapper.selectById(orderId);
    }

    @Override
    public RideOrder getActiveOrderByUserId(Long userId) {
        return rideOrderMapper.selectActiveOrderByUserId(userId);
    }

    @Override
    public long getOrderCount() {
        return rideOrderMapper.selectCount();
    }

    @Override
    public boolean updateOrder(RideOrder order) {
        return rideOrderMapper.update(order) > 0;
    }
}
