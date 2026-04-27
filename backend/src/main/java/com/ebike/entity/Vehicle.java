package com.ebike.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Vehicle {
    private Long id;
    private String vehicleCode;
    private String vehicleType;
    private Integer status;
    private String statusName;
    private Long currentStationId;
    private Station currentStation;
    private Integer batteryLevel;
    private Date createTime;
    private Date lastMaintenanceTime;
}
