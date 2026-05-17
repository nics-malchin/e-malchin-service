CREATE TABLE IF NOT EXISTS tracker_device (
    id             INT          NOT NULL AUTO_INCREMENT,
    imei           VARCHAR(20)  NOT NULL,
    name           VARCHAR(100) NOT NULL,
    device_model   VARCHAR(50),
    notes          TEXT,
    livestock_id   INT,
    active         TINYINT(1)   NOT NULL DEFAULT 1,
    registered_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_tracker_device  PRIMARY KEY (id),
    CONSTRAINT uq_tracker_device_imei UNIQUE (imei)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
