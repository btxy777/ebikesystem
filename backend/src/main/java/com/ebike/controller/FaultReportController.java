package com.ebike.controller;

import com.ebike.common.JwtUtils;
import com.ebike.common.Response;
import com.ebike.entity.FaultReport;
import com.ebike.service.FaultReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fault-reports")
public class FaultReportController {

    @Autowired
    private FaultReportService faultReportService;

    @PostMapping("/create")
    public Response<Map<String, Object>> createFaultReport(@RequestBody Map<String, Object> params, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Long userId = JwtUtils.getUserId(token);
            Long vehicleId = params.get("vehicleId") != null ? Long.valueOf(params.get("vehicleId").toString()) : null;
            String description = params.get("description") != null ? params.get("description").toString() : null;

            if (vehicleId == null || description == null || description.trim().isEmpty()) {
                return Response.error("车辆和描述不能为空");
            }

            boolean result = faultReportService.createFaultReport(userId, vehicleId, description);
            if (result) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("message", "故障报修提交成功");
                return Response.success(resultMap);
            } else {
                return Response.error("故障报修提交失败");
            }
        } catch (Exception e) {
            return Response.error("操作失败");
        }
    }

    @GetMapping("/list")
    public Response<Map<String, Object>> getAllFaultReports(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2 && role != 3) {
                return Response.error("只有运维人员或管理员可以查看故障单列表");
            }
            List<FaultReport> reports = faultReportService.getAllFaultReports();
            reports.forEach(this::convertStatus);
            int total = reports.size();
            Map<String, Object> result = new HashMap<>();
            if (total == 0) {
                result.put("data", reports);
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
            List<FaultReport> pageData = reports.subList(start, end);
            result.put("data", pageData);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            return Response.success(result);
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @GetMapping("/listByStatus")
    public Response<List<FaultReport>> getFaultReportsByStatus(Integer status, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2 && role != 3) {
                return Response.error("只有运维人员或管理员可以查看故障单列表");
            }
            if (status == null) {
                return Response.error("状态参数不能为空");
            }
            List<FaultReport> reports = faultReportService.getFaultReportsByStatus(status);
            reports.forEach(this::convertStatus);
            return Response.success(reports);
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @GetMapping("/listByOperator/{operatorId}")
    public Response<List<FaultReport>> getFaultReportsByOperator(@PathVariable Long operatorId, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2 && role != 3) {
                return Response.error("只有运维人员或管理员可以查看");
            }
            List<FaultReport> reports = faultReportService.getFaultReportsByOperatorId(operatorId);
            reports.forEach(this::convertStatus);
            return Response.success(reports);
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @PostMapping("/accept")
    public Response<Map<String, Object>> acceptFaultReport(@RequestBody Map<String, Long> params, @RequestHeader("Authorization") String token) {
        System.out.println("=== Accept Controller ===");
        System.out.println("params: " + params);
        try {
            token = token.substring(7);
            System.out.println("Token parsed in controller");
            Integer role = JwtUtils.getRole(token);
            System.out.println("Role: " + role);
            if (role != 2 && role != 3) {
                System.out.println("Role check failed");
                return Response.error("只有运维人员或管理员可以接单");
            }

            Long reportId = params.get("reportId");
            Long operatorId = params.get("operatorId");
            System.out.println("reportId: " + reportId + ", operatorId: " + operatorId);

            if (reportId == null || operatorId == null) {
                System.out.println("Null check failed");
                return Response.error("报修单ID和运维人员ID不能为空");
            }

            boolean result = faultReportService.acceptFaultReport(reportId, operatorId);
            System.out.println("Service result: " + result);
            if (result) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("message", "接单成功");
                return Response.success(resultMap);
            } else {
                return Response.error("接单失败，报修单不存在或已处理");
            }
        } catch (Exception e) {
            System.out.println("Exception in controller: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Token解析失败");
        }
    }

    @PostMapping("/updateStatus")
    public Response<Map<String, Object>> updateFaultStatus(@RequestBody Map<String, Object> params, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2) {
                return Response.error("只有运维人员可以更新故障单状态");
            }

            Long reportId = Long.valueOf(params.get("reportId").toString());
            Integer status = Integer.valueOf(params.get("status").toString());
            Long operatorId = Long.valueOf(params.get("operatorId").toString());

            boolean result = faultReportService.updateFaultStatus(reportId, status, operatorId);
            if (result) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("message", "状态更新成功");
                return Response.success(resultMap);
            } else {
                return Response.error("状态更新失败");
            }
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @PostMapping("/repair")
    public Response<Map<String, Object>> repairAndResolve(@RequestBody Map<String, Long> params, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2) {
                return Response.error("只有运维人员可以修复车辆");
            }

            Long reportId = params.get("reportId");
            Long operatorId = params.get("operatorId");

            if (reportId == null || operatorId == null) {
                return Response.error("报修单ID和运维人员ID不能为空");
            }

            boolean result = faultReportService.repairVehicleAndResolve(reportId, operatorId);
            if (result) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("message", "车辆已修复并设置为可用");
                return Response.success(resultMap);
            } else {
                return Response.error("操作失败，报修单不存在或已完成");
            }
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @GetMapping("/detail")
    public Response<FaultReport> getFaultReportDetail(Long id, @RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2 && role != 3) {
                return Response.error("只有运维人员或管理员可以查看故障单详情");
            }
            if (id == null) {
                return Response.error("故障单ID不能为空");
            }
            FaultReport report = faultReportService.getFaultReportById(id);
            if (report != null) {
                convertStatus(report);
                return Response.success(report);
            } else {
                return Response.error("故障单不存在");
            }
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @GetMapping("/lowBattery")
    public Response<List<FaultReport>> getLowBatteryFaultReports(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 2 && role != 3) {
                return Response.error("只有运维人员或管理员可以查看");
            }
            List<FaultReport> reports = faultReportService.getLowBatteryFaultReports();
            reports.forEach(this::convertStatus);
            return Response.success(reports);
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    private void convertStatus(FaultReport report) {
        if (report.getStatus() != null) {
            switch (report.getStatus()) {
                case 0: report.setStatusName("待处理"); break;
                case 1: report.setStatusName("处理中"); break;
                case 2: report.setStatusName("已解决"); break;
                default: report.setStatusName("未知");
            }
        }
    }
}
