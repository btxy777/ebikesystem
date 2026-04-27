package com.ebike.mapper;

import com.ebike.entity.FaultReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FaultReportMapper {
    int insert(FaultReport faultReport);
    int update(FaultReport faultReport);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("operatorId") Long operatorId, @Param("resolveTime") java.util.Date resolveTime);
    FaultReport selectById(Long id);
    List<FaultReport> selectAll();
    List<FaultReport> selectByStatus(Integer status);
    List<FaultReport> selectByOperatorId(Long operatorId);
    List<FaultReport> selectByVehicleId(Long vehicleId);
    long selectCount();
    List<FaultReport> selectByLowBattery();
}
