package com.ebike.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Station {
    private Long id;
    private String stationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Integer capacity;
    private Integer availableSlots;
    private Date createTime;
}
