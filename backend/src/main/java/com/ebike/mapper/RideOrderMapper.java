package com.ebike.mapper;

import com.ebike.entity.RideOrder;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RideOrderMapper {
    int insert(RideOrder rideOrder);
    int update(RideOrder rideOrder);
    RideOrder selectById(Long id);
    List<RideOrder> selectAll();
    List<RideOrder> selectByUserId(Long userId);
    RideOrder selectActiveOrderByUserId(Long userId);
    List<RideOrder> selectByVehicleId(Long vehicleId);
    long selectCount();
}
