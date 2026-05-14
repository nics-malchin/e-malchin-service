-- =============================================================
-- NICS Demo Seed — Малчин user_id=6
-- Хонь / Ямаа / Адуу + GPS + Эрүүл мэндийн бүртгэл
--
-- Ажиллуулах: mysql -u <user> -p nics < seed_user6_demo.sql
--   эсвэл phpMyAdmin → Import
--
-- LivestockType ordinal (EnumType.ORDINAL):
--   HORSE=0  CATTLE=1  CAMEL=2  SHEEP=3  GOAT=4
-- sex: 0=эм, 1=эр
-- =============================================================

-- -------------------------------------------------------------
-- 1. LIVESTOCK  (3 мал — user_id=6)
-- -------------------------------------------------------------
INSERT INTO nics.livestock
    (code, age, weight, type, view, user_id, sex,
     livestock_id, parent_id, have_child, health_id,
     active_flag, status, created_date, created_by, updated_date, updated_by)
VALUES
    -- Хонь
    ('SHP-2026-001', 3, 42.5, 3, NULL, 6, 0,
     10001, NULL, 1, NULL,
     1, 0, NOW(), 6, NOW(), 6),

    -- Ямаа
    ('GOT-2026-001', 2, 31.0, 4, NULL, 6, 1,
     10002, NULL, 0, NULL,
     1, 0, NOW(), 6, NOW(), 6),

    -- Адуу (GPS холбогдох мал)
    ('HRS-2026-001', 5, 310.0, 0, NULL, 6, 1,
     10003, NULL, 0, NULL,
     1, 0, NOW(), 6, NOW(), 6);

-- -------------------------------------------------------------
-- 2. GPS DEVICE  (Адуу дээр — livestock_id нь HRS-2026-001-ийн id)
-- -------------------------------------------------------------
-- Адуугийн livestock.id-г олж GPS холбоно
INSERT INTO nics.gps_device
    (livestock_id, device_code, latitude, longitude, altitude, recorded_at, battery_level,
     active_flag, status, created_date, created_by, updated_date, updated_by)
SELECT
    l.id,
    '866971060123456',   -- Traccar IMEI
    49.4686,             -- Дархан орчмын координат
    105.9745,
    920.0,
    NOW(),
    87,
    1, 0, NOW(), 6, NOW(), 6
FROM nics.livestock l
WHERE l.code = 'HRS-2026-001'
  AND l.user_id = 6
LIMIT 1;

-- -------------------------------------------------------------
-- 3. ANIMAL HEALTH  (мал бүрт нэг эрүүл мэндийн бүртгэл)
-- -------------------------------------------------------------

-- 3-а. Хонь — ханиад, хамрын гоожилт
INSERT INTO nics.animal_health
    (livestock_id, diagnosed_by, examination_date, symptoms, diagnosis,
     treatment, next_check_date, notes,
     active_flag, status, created_date, created_by, updated_date, updated_by)
SELECT
    l.id,
    6,
    '2026-04-10',
    'Хамрын гоожилт, ханиад, идэш муу идэж байна',
    'Вирусын амьсгалын замын халдвар (BRSV)',
    'Окситетрациклин 20 мг/кг/өдөр 5 хоног, амрах орчин хангах',
    '2026-04-25',
    'Сүргийн 3 хонинд ижил шинж тэмдэг гарсан. Тусгаарлав.',
    1, 0, NOW(), 6, NOW(), 6
FROM nics.livestock l
WHERE l.code = 'SHP-2026-001' AND l.user_id = 6 LIMIT 1;

-- 3-б. Ямаа — гэдэсний хямрал
INSERT INTO nics.animal_health
    (livestock_id, diagnosed_by, examination_date, symptoms, diagnosis,
     treatment, next_check_date, notes,
     active_flag, status, created_date, created_by, updated_date, updated_by)
SELECT
    l.id,
    6,
    '2026-04-18',
    'Суулгалт, биеийн жин буурсан, идэш татгалзаж байна',
    'Гэдэсний коли халдвар (E.coli)',
    'Норфлоксацин 10 мг/кг 3 хоног, давсжуулалт, ORS уух',
    '2026-05-03',
    'Бэлчээр солих шаардлагатай. Ус цэвэр байлгах.',
    1, 0, NOW(), 6, NOW(), 6
FROM nics.livestock l
WHERE l.code = 'GOT-2026-001' AND l.user_id = 6 LIMIT 1;

-- 3-в. Адуу — хөл хавдар
INSERT INTO nics.animal_health
    (livestock_id, diagnosed_by, examination_date, symptoms, diagnosis,
     treatment, next_check_date, notes,
     active_flag, status, created_date, created_by, updated_date, updated_by)
SELECT
    l.id,
    6,
    '2026-05-02',
    'Урд хөлийн хавдар, таашлагдахгүй явж байна, халуун',
    'Тендинит (шилбэний шөрмөсний үрэвсэл)',
    'Фенилбутазон 4.4 мг/кг 5 хоног, хүйтэн жин тавих, амрах',
    '2026-05-17',
    'GPS collar хүнд байж болзошгүй — жинг дахин шалгана.',
    1, 0, NOW(), 6, NOW(), 6
FROM nics.livestock l
WHERE l.code = 'HRS-2026-001' AND l.user_id = 6 LIMIT 1;

-- -------------------------------------------------------------
-- 4. ШАЛГАЛТ — оруулсан өгөгдлийг харах
-- -------------------------------------------------------------

SELECT
    l.id          AS livestock_id,
    l.code        AS mal_code,
    CASE l.type
        WHEN 0 THEN 'Адуу'
        WHEN 3 THEN 'Хонь'
        WHEN 4 THEN 'Ямаа'
        ELSE CONCAT('type=', l.type)
    END           AS mal_torол,
    l.age         AS nas,
    l.weight      AS jing_kg,
    CASE l.sex WHEN 0 THEN 'Эм' WHEN 1 THEN 'Эр' END AS hүйs,
    g.device_code AS gps_imei,
    g.latitude    AS lat,
    g.longitude   AS lon,
    g.battery_level AS battery,
    ah.examination_date AS shinjilegdsen_ognoo,
    ah.diagnosis  AS onoshilgoo,
    ah.next_check_date  AS daraagiin_ognoo
FROM nics.livestock l
LEFT JOIN nics.gps_device   g  ON g.livestock_id = l.id
LEFT JOIN nics.animal_health ah ON ah.livestock_id = l.id
WHERE l.user_id = 6
  AND l.code IN ('SHP-2026-001','GOT-2026-001','HRS-2026-001')
ORDER BY l.type;
