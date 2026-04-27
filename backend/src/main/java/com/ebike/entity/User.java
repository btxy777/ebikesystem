package com.ebike.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private Integer role;
    private Integer status;
    private Date createTime;
}
