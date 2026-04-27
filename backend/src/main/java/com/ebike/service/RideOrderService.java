package com.ebike.service;

import com.ebike.entity.RideOrder;
import java.util.List;

public interface RideOrderService {
    RideOrder startRide(Long userId, Long vehicleId, Long stationId);
    RideOrder startRideWithMessage(Long userId, Long vehicleId, Long stationId);
    RideOrder endRide(Long orderId, Long stationId);
    List<RideOrder> getAllOrders();
    List<RideOrder> getOrdersByUserId(Long userId);
    RideOrder getOrderById(Long orderId);
    RideOrder getActiveOrderByUserId(Long userId);
    boolean updateOrder(RideOrder order);
    long getOrderCount();
}
