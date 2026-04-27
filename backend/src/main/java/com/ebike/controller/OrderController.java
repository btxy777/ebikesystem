package com.ebike.controller;

import com.ebike.common.JwtUtils;
import com.ebike.common.Response;
import com.ebike.entity.RideOrder;
import com.ebike.entity.Vehicle;
import com.ebike.service.RideOrderService;
import com.ebike.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RideOrderService rideOrderService;

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/start")
    public Response<Map<String, Object>> startRide(@RequestBody Map<String, Long> params, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Long userId = JwtUtils.getUserId(token);
            Long vehicleId = params.get("vehicleId");

            if (vehicleId == null) {
                return Response.error("车辆ID不能为空");
            }

            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return Response.error("车辆不存在");
            }
            if (vehicle.getStatus() != 1) {
                return Response.error("车辆不可用");
            }

            RideOrder order = rideOrderService.startRideWithMessage(userId, vehicleId, vehicle.getCurrentStationId());
            if (order != null) {
                return Response.success(buildOrderResult(order, "开始用车成功"));
            }
            return Response.error("开始用车失败");
        } catch (Exception e) {
            return Response.error("操作失败");
        }
    }

    @PostMapping("/end")
    public Response<Map<String, Object>> endRide(@RequestBody Map<String, Long> params, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Long userId = JwtUtils.getUserId(token);
            Long orderId = params.get("orderId");

            if (orderId == null) {
                return Response.error("订单ID不能为空");
            }

            RideOrder order = rideOrderService.endRide(orderId, null);
            if (order != null) {
                Map<String, Object> result = buildOrderResult(order, "结束用车成功");
                result.put("duration", order.getDuration());
                result.put("endStationId", order.getEndStationId());
                return Response.success(result);
            } else {
                return Response.error("结束用车失败，订单不存在或已完成");
            }
        } catch (Exception e) {
            return Response.error("操作失败");
        }
    }

    @GetMapping("/my")
    public Response<List<RideOrder>> getMyOrders(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Long userId = JwtUtils.getUserId(token);
            List<RideOrder> orders = rideOrderService.getOrdersByUserId(userId);
            return Response.success(orders);
        } catch (Exception e) {
            return Response.error("获取订单失败");
        }
    }

    @GetMapping("/all")
    public Response<List<RideOrder>> getAllOrders() {
        List<RideOrder> orders = rideOrderService.getAllOrders();
        return Response.success(orders);
    }

    @GetMapping("/list")
    public Response<Map<String, Object>> getOrderList(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<RideOrder> orders;
        if (userId != null) {
            orders = rideOrderService.getOrdersByUserId(userId);
        } else {
            orders = rideOrderService.getAllOrders();
        }
        int total = orders.size();
        Map<String, Object> result = new HashMap<>();
        if (total == 0) {
            result.put("data", orders);
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
        List<RideOrder> pageData = orders.subList(start, end);
        result.put("data", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Response.success(result);
    }

    @GetMapping("/detail")
    public Response<RideOrder> getOrderDetail(Long orderId) {
        if (orderId == null) {
            return Response.error("订单ID不能为空");
        }
        RideOrder order = rideOrderService.getOrderById(orderId);
        if (order != null) {
            return Response.success(order);
        } else {
            return Response.error("订单不存在");
        }
    }

    @GetMapping("/active")
    public Response<RideOrder> getActiveOrder(Long userId) {
        if (userId == null) {
            return Response.error("用户ID不能为空");
        }
        RideOrder order = rideOrderService.getActiveOrderByUserId(userId);
        return Response.success(order);
    }

    @PutMapping("/update")
    public Response<String> updateOrder(@RequestBody RideOrder order) {
        if (order.getId() == null) {
            return Response.error("订单ID不能为空");
        }
        RideOrder existing = rideOrderService.getOrderById(order.getId());
        if (existing == null) {
            return Response.error("订单不存在");
        }
        existing.setStatus(order.getStatus());
        existing.setEndStationId(order.getEndStationId());
        existing.setEndTime(order.getEndTime());
        existing.setDuration(order.getDuration());
        boolean success = rideOrderService.updateOrder(existing);
        if (success) {
            return Response.success("更新成功");
        }
        return Response.error("更新失败");
    }

    private Map<String, Object> buildOrderResult(RideOrder order, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("userId", order.getUserId());
        result.put("vehicleId", order.getVehicleId());
        result.put("startStationId", order.getStartStationId());
        result.put("startTime", order.getStartTime());
        result.put("status", order.getStatus());
        String statusName = "进行中";
        if (order.getStatus() == 1) {
            statusName = "已完成";
        } else if (order.getStatus() == 2) {
            statusName = "已取消";
        }
        result.put("statusName", statusName);
        return result;
    }
}