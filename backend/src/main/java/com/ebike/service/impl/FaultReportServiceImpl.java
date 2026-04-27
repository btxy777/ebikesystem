package com.ebike.service.impl;

import com.ebike.entity.FaultReport;
import com.ebike.entity.Vehicle;
import com.ebike.mapper.FaultReportMapper;
import com.ebike.mapper.VehicleMapper;
import com.ebike.service.FaultReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FaultReportServiceImpl implements FaultReportService {

    @Autowired
    private FaultReportMapper faultReportMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_PROCESSING = 1;
    private static final int STATUS_RESOLVED = 2;

    private static final int VEHICLE_STATUS_IDLE = 1;
    private static final int VEHICLE_STATUS_MAINTENANCE = 4;

    @Override
    public boolean createFaultReport(Long userId, Long vehicleId, String description) {
        FaultReport report = new FaultReport();
        report.setUserId(userId);
        report.setVehicleId(vehicleId);
        report.setDescription(description);
        report.setStatus(STATUS_PENDING);
        report.setCreateTime(new Date());

        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle != null) {
            vehicle.setStatus(VEHICLE_STATUS_MAINTENANCE);
            vehicleMapper.update(vehicle);
        }

        return faultReportMapper.insert(report) > 0;
    }

    @Override
    public boolean acceptFaultReport(Long reportId, Long operatorId) {
        FaultReport report = faultReportMapper.selectById(reportId);
        if (report == null || report.getStatus() != STATUS_PENDING) {
            return false;
        }
        return faultReportMapper.updateStatus(reportId, STATUS_PROCESSING, operatorId, null) > 0;
    }

    @Override
    public boolean updateFaultStatus(Long reportId, Integer status, Long operatorId) {
        FaultReport report = faultReportMapper.selectById(reportId);
        if (report == null) {
            return false;
        }
        Date resolveTime = (status == STATUS_RESOLVED) ? new Date() : null;
        return faultReportMapper.updateStatus(reportId, status, operatorId, resolveTime) > 0;
    }

    @Override
    public boolean repairVehicleAndResolve(Long reportId, Long operatorId) {
        FaultReport report = faultReportMapper.selectById(reportId);
        if (report == null || report.getStatus() == STATUS_RESOLVED) {
            return false;
        }

        boolean updated = faultReportMapper.updateStatus(reportId, STATUS_RESOLVED, operatorId, new Date()) > 0;
        if (updated) {
            Vehicle vehicle = vehicleMapper.selectById(report.getVehicleId());
            if (vehicle != null) {
                vehicleMapper.updateStatusAndStation(vehicle.getId(), VEHICLE_STATUS_IDLE, vehicle.getCurrentStationId());
            }
        }
        return updated;
    }

    @Override
    public List<FaultReport> getAllFaultReports() {
        return faultReportMapper.selectAll();
    }

    @Override
    public List<FaultReport> getFaultReportsByStatus(Integer status) {
        return faultReportMapper.selectByStatus(status);
    }

    @Override
    public List<FaultReport> getFaultReportsByOperatorId(Long operatorId) {
        return faultReportMapper.selectByOperatorId(operatorId);
    }

    @Override
    public FaultReport getFaultReportById(Long id) {
        return faultReportMapper.selectById(id);
    }

    @Override
    public long getFaultCount() {
        return faultReportMapper.selectCount();
    }

    @Override
    public List<FaultReport> getLowBatteryFaultReports() {
        return faultReportMapper.selectByLowBattery();
    }
}
