-- =========================
-- 共享电动车系统（最终版）
-- =========================

DROP DATABASE IF EXISTS ebike_system;
CREATE DATABASE ebike_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ebike_system;

-- =========================
-- 1. 用户表（含角色）
-- =========================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    role TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-用户，2-运维，3-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =========================
-- 2. 站点表
-- =========================
CREATE TABLE stations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '站点ID',
    station_name VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 6) NOT NULL,
    longitude DECIMAL(10, 6) NOT NULL,
    address VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    available_slots INT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点表';

-- =========================
-- 3. 车辆表（含电量+位置）
-- =========================
CREATE TABLE vehicles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车辆ID',
    vehicle_code VARCHAR(50) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0-离线 1-空闲 2-使用中 3-故障 4-维修中',
    current_station_id BIGINT,
    battery_level INT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_maintenance_time DATETIME,
    FOREIGN KEY (current_station_id) REFERENCES stations(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- =========================
-- 4. 订单表
-- =========================
CREATE TABLE ride_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    start_station_id BIGINT NOT NULL,
    end_station_id BIGINT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration INT,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-进行中 1-已完成 2-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    FOREIGN KEY (start_station_id) REFERENCES stations(id) ON DELETE CASCADE,
    FOREIGN KEY (end_station_id) REFERENCES stations(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =========================
-- 5. 故障报修表（核心新增）
-- =========================
CREATE TABLE fault_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '故障单ID',
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    description VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-待处理 1-处理中 2-已解决',
    operator_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolve_time DATETIME,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (operator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故障报修表';

-- =========================
-- 索引
-- =========================
CREATE INDEX idx_vehicle_status ON vehicles(status);
CREATE INDEX idx_vehicle_station ON vehicles(current_station_id);
CREATE INDEX idx_order_user ON ride_orders(user_id);
CREATE INDEX idx_order_status ON ride_orders(status);

-- =========================
-- 初始化数据
-- =========================

-- 用户
INSERT INTO users (username, password, phone, role) VALUES
('user1', '123456', '13800000001', 1),
('user2', '123456', '13800000002', 1),
('operator1', '123456', '13800000003', 2),
('admin1', '123456', '13800000004', 3);

-- 站点
INSERT INTO stations (station_name, latitude, longitude, address, capacity, available_slots) VALUES
('东门', 23.129110, 113.264385, '学校东门', 20, 10),
('图书馆', 23.130000, 113.265000, '图书馆门口', 15, 8),
('宿舍区', 23.131000, 113.266000, '学生宿舍', 25, 12),
('体育馆', 23.132000, 113.267000, '体育馆旁', 18, 9);

-- 车辆
INSERT INTO vehicles (vehicle_code, vehicle_type, status, current_station_id, battery_level) VALUES
('EB001', '电动车', 1, 1, 80),
('EB002', '电动车', 1, 1, 60),
('EB003', '电动车', 2, NULL, 70),
('EB004', '电动车', 3, 2, 30),
('EB005', '电动车', 1, 3, 90);

-- 订单
INSERT INTO ride_orders 
(user_id, vehicle_id, start_station_id, end_station_id, start_time, end_time, duration, status)
VALUES
(1, 1, 1, 2, NOW() - INTERVAL 1 HOUR, NOW(), 60, 1),
(2, 2, 2, 3, NOW() - INTERVAL 30 MINUTE, NOW(), 30, 1),
(1, 3, 1, NULL, NOW() - INTERVAL 10 MINUTE, NULL, NULL, 0);

-- 故障
INSERT INTO fault_reports (user_id, vehicle_id, description, status, operator_id) VALUES
(1, 4, '车辆无法启动', 0, NULL),
(2, 4, '电池异常', 1, 3),
(1, 4, '刹车失灵', 2, 3);