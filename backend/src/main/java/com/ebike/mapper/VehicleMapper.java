package com.ebike.mapper;

import com.ebike.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VehicleMapper {
    List<Vehicle> selectAll();
    Vehicle selectById(Long id);
    List<Vehicle> selectByStatus(Integer status);
    List<Vehicle> selectByStationId(Long stationId);
    int update(Vehicle vehicle);
    int updateStatusAndStation(@Param("id") Long id, @Param("status") Integer status, @Param("stationId") Long stationId);
    int updateBatteryLevel(@Param("id") Long id, @Param("batteryLevel") Integer batteryLevel);
    int insert(Vehicle vehicle);
    long selectCount();
    int selectFaultCount();
    int selectLowBatteryCount();
}
