package com.ebike.service;

import com.ebike.entity.FaultReport;
import java.util.List;

public interface FaultReportService {
    boolean createFaultReport(Long userId, Long vehicleId, String description);
    boolean acceptFaultReport(Long reportId, Long operatorId);
    boolean updateFaultStatus(Long reportId, Integer status, Long operatorId);
    boolean repairVehicleAndResolve(Long reportId, Long operatorId);
    List<FaultReport> getAllFaultReports();
    List<FaultReport> getFaultReportsByStatus(Integer status);
    List<FaultReport> getFaultReportsByOperatorId(Long operatorId);
    FaultReport getFaultReportById(Long id);
    long getFaultCount();
    List<FaultReport> getLowBatteryFaultReports();
}
