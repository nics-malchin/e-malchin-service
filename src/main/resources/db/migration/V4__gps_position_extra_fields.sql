ALTER TABLE gps_position
    ADD COLUMN sats          INT    NULL AFTER altitude,
    ADD COLUMN battery_volt  DOUBLE NULL AFTER sats,
    ADD COLUMN signal_level  INT    NULL AFTER battery_volt;
