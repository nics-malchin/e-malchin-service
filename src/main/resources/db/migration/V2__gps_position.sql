CREATE TABLE IF NOT EXISTS gps_position (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    imei       VARCHAR(20)  NOT NULL,
    latitude   DOUBLE       NOT NULL,
    longitude  DOUBLE       NOT NULL,
    speed      DOUBLE,
    course     DOUBLE,
    altitude   DOUBLE,
    fix_time   DATETIME(0)  NOT NULL,
    synced_at  DATETIME     NULL,
    CONSTRAINT pk_gps_position              PRIMARY KEY (id),
    CONSTRAINT uq_gps_position_imei_fixtime UNIQUE (imei, fix_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gps_position_imei_fixtime ON gps_position (imei, fix_time);
