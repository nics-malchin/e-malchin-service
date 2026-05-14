-- =============================================================
-- Малын дугаарын формат: AASSNNNNNN (10 оронтой)
-- АА=аймаг(14), СС=сум(16), NNNNNN=дараалал(6)
-- user_id=6, SHP/GOT/HRS аль хэдийн 000001-000003 авсан
-- → 14 шинэ мал: 1416000004 - 1416000017
-- =============================================================

INSERT INTO livestock (code, age, weight, type, view, user_id, sex,
    livestock_id, parent_id, have_child, health_id,
    active_flag, status, created_date, created_by, updated_date, updated_by)
VALUES
  ('1416000004',4,280.0,1,NULL,6,1,20001,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000005',3,265.0,1,NULL,6,0,20002,NULL,1,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000006',5,310.0,1,NULL,6,1,20003,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000007',2,210.0,1,NULL,6,0,20004,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000008',6,340.0,1,NULL,6,1,20005,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000009',4,290.0,1,NULL,6,0,20006,NULL,1,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000010',3,45.0, 3,NULL,6,0,20007,NULL,1,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000011',2,38.0, 3,NULL,6,1,20008,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000012',4,51.0, 3,NULL,6,0,20009,NULL,1,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000013',3,42.0, 3,NULL,6,1,20010,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000014',2,28.0, 4,NULL,6,0,20011,NULL,1,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000015',3,32.0, 4,NULL,6,1,20012,NULL,0,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000016',4,30.0, 4,NULL,6,0,20013,NULL,1,NULL,1,0,NOW(),6,NOW(),6),
  ('1416000017',2,25.0, 4,NULL,6,1,20014,NULL,0,NULL,1,0,NOW(),6,NOW(),6);

-- localhistory → lab_result (6 тест/мал, 14 мал)
INSERT INTO lab_result (livestock_id, sample_date, result_date, test_type, lab_name,
    result, certified, notes, active_flag, status, created_date, created_by, updated_date, updated_by)
SELECT
  l.id,
  DATE(h.nTestTime), DATE(h.nTestTime),
  h.ItemName,
  'БАХ Мал эмнэлгийн оношилгооны лаборатори',
  CASE WHEN h.JudgeString LIKE '%Positive%' THEN 'Эерэг (+)' ELSE 'Сөрөг (-)' END,
  CASE WHEN h.JudgeString LIKE '%Positive%' THEN 1 ELSE 0 END,
  CONCAT('SampleID: ', h.SampleID),
  1, 0, NOW(), 6, NOW(), 6
FROM (
  SELECT *, CEIL(ROW_NUMBER() OVER (ORDER BY SampleID) / 6) AS animal_num
  FROM localhistory
) h
JOIN livestock l
  ON l.code = CONCAT('1416', LPAD(h.animal_num + 3, 6, '0'))
  AND l.user_id = 6;

-- Шалгалт
SELECT l.code,
  CASE l.type WHEN 1 THEN 'Үхэр' WHEN 3 THEN 'Хонь' WHEN 4 THEN 'Ямаа' END as torол,
  COUNT(lr.id) as tests,
  SUM(lr.certified) as positive_count
FROM livestock l
JOIN lab_result lr ON lr.livestock_id = l.id
WHERE l.user_id = 6 AND l.code LIKE '1416%'
GROUP BY l.code, l.type ORDER BY l.code;
