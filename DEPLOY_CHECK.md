# Deploy шалгах гарын авлага

## Энэ deploy-д хийгдсэн өөрчлөлтүүд

### Backend (e-malchin-service) — commit dccf26f
- `GpsPositionSyncService` — Traccar лог файл уншихаас **Traccar HTTP API** руу бүрэн дахин бичсэн
  - `GET /api/devices` → бүх device жагсаана
  - `GET /api/reports/route?deviceId=X&from=...&to=...` → байршлын түүх татна
  - Rolling window: сүүлийн мэдэгдсэн fix_time - 5 мин эсвэл now - 2 цаг
  - Batch INSERT IGNORE — давхардал автомат хасагдана
- `TraccarService.getRoute` — `deviceId` эсвэл `imei` аль нэгээр ажиллана (synthetic device дэмжинэ)
- `TraccarController` — `/backfill` endpoint-уудыг хасав
- `docker-compose.prod.yml` — traccar-logs volume хасав, `TRACCAR_SYNC_HISTORY_MINUTES` нэмэв
- `application-prod.properties` — `traccar.url` заавал env var болгосон, `traccar.log.path` хасав

### Frontend (portal-malchin-gui) — commit bde966c
- GPS map дээр live device marker харуулах болсон (ногоон=online, саарал=offline)
- Бүх device-н trajectory ажиллах болсон (synthetic/unregistered device-г imei-аар дамжуулна)
- Landing page — Lorem ipsum-г бодит И-Малчин агуулгаар солисон

---

## Production .env-д заавал байх ёстой утгууд

```env
SPRING_DATASOURCE_URL=jdbc:mysql://...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...

KEYCLOAK_CLIENT_SECRET=...
KEYCLOAK_ADMIN_USERNAME=superadmin
KEYCLOAK_ADMIN_PASSWORD=...

TRACCAR_URL=http://<TRACCAR_SERVER_IP>:8082    # ← заавал бодит IP
TRACCAR_EMAIL=emalchinnics@gmail.com
TRACCAR_PASSWORD=...                            # ← заавал

TRACCAR_SYNC_INTERVAL_MS=60000
TRACCAR_SYNC_HISTORY_MINUTES=120
```

---

## Сервер дээр шалгах командууд

### 1. Container төлөв
```bash
docker ps -a
docker compose -f docker-compose.prod.yml ps
```

### 2. Backend log
```bash
docker logs emalchin_backend --tail 100
docker logs emalchin_backend --tail 50 -f
```

### 3. Startup амжилттай болсон эсэх
```bash
docker logs emalchin_backend 2>&1 | grep -E "Started|ERROR|HikariPool|Flyway"
```

### 4. GPS sync ажиллаж байгаа эсэх
```bash
docker logs emalchin_backend 2>&1 | grep -i "GPS sync"
```

### 5. Traccar холболт сервер дотроос шалгах
```bash
source .env
docker exec emalchin_backend curl -s \
  -u "${TRACCAR_EMAIL}:${TRACCAR_PASSWORD}" \
  "${TRACCAR_URL}/api/devices" | head -c 500
```

### 6. Backend API амьд эсэх
```bash
curl -s http://localhost:8081/actuator/health
curl -s http://localhost:8081/api/tracker/positions | head -c 300
```

### 7. Deploy дахин хийх (шаардлагатай бол)
```bash
git pull origin master
docker compose -f docker-compose.prod.yml up -d --build
```

---

## Нийтлэг алдаа ба шийдэл

| Алдаа | Шалтгаан | Шийдэл |
|---|---|---|
| `traccar.url` — connection refused | TRACCAR_URL буруу эсвэл тохируулаагүй | `.env`-д зөв IP тохируулна |
| `GPS sync: no devices returned` | Traccar credentials буруу | `TRACCAR_EMAIL`/`TRACCAR_PASSWORD` шалгана |
| HikariPool timeout | DB хүрч чадахгүй | `SPRING_DATASOURCE_URL` шалгана |
| Container restart loop | Env var дутуу | `docker logs` харна |
