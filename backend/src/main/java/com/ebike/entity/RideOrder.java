package com.ebike.entity;

import lombok.Data;
import java.util.Date;

@Data
public class RideOrder {
    private Long id;
    private Long userId;
    private User user;
    private Long vehicleId;
    private Vehicle vehicle;
    private Long startStationId;
    private Station startStation;
    private Long endStationId;
    private Station endStation;
    private Date startTime;
    private Date endTime;
    private Integer duration;
    private Integer status;
    private Date createTime;
}
