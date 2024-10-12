DROP TABLE IF EXISTS tb_lb_health_check;

CREATE TABLE tb_lb_health_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pool VARCHAR(255),
    rs_ip VARCHAR(255),
    status VARCHAR(10),
    timestamp TIMESTAMP
);

DROP TABLE IF EXISTS tb_alarm_log;

CREATE TABLE tb_alarm_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pool VARCHAR(255),
    rs_ip VARCHAR(255),
    alarm_message VARCHAR(1024),
    status VARCHAR(20),
    timestamp TIMESTAMP
);
