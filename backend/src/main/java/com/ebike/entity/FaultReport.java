package com.ebike.entity;

import lombok.Data;
import java.util.Date;

@Data
public class FaultReport {
    private Long id;
    private Long userId;
    private User user;
    private Long vehicleId;
    private Vehicle vehicle;
    private String description;
    private Integer status;
    private String statusName;
    private Long operatorId;
    private User operator;
    private Date createTime;
    private Date resolveTime;
}
