<h1 align="center">
  🏠 Nestify
</h1>

<h4 align="center">Ev arkadaşlarının hayatını kolaylaştıran tam kapsamlı ev yönetim platformu.</h4>

<p align="center">
  <a href="https://nestify.hamdiyecicek.tech" target="_blank">
    <img src="https://img.shields.io/badge/Canlı%20Demo-nestify.hamdiyecicek.tech-6D28D9?style=for-the-badge&logo=vercel&logoColor=white" alt="Canlı Demo">
  </a>
  &nbsp;
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  &nbsp;
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React">
  &nbsp;
  <img src="https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  &nbsp;
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
</p>

<p align="center">
  <a href="#-hakkında">Hakkında</a> •
  <a href="#-özellikler">Özellikler</a> •
  <a href="#-teknoloji-yığını">Teknoloji Yığını</a> •
  <a href="#-mimari">Mimari</a> •
  <a href="#-başlarken">Başlarken</a> •
  <a href="#-api-dokümantasyonu">API Dökümanı</a> •
  <a href="#-cicd-pipeline">CI/CD</a>
</p>

---

## 🎯 Hakkında

**Nestify**, birlikte ev paylaşan kişiler için geliştirilmiş, üretime hazır bir full-stack web uygulamasıdır. Ortak giderlerden etkinlik takvimine, ev ihtiyaçlarından üye rollerine kadar her şeyi tek bir temiz ve duyarlı arayüzde yönetmenizi sağlar.

> **Neden bu projeyi yaptım:** Tutorial projelerinin ötesine geçmek ve veri tabanı şemasından CI/CD pipeline'ına kadar gerçek dünyaya ait bir uygulamayı uçtan uca tasarlayıp, geliştirip, deploy edebildiğimi göstermek istedim.

**Canlı Demo → [nestify.hamdiyecicek.tech](https://nestify.hamdiyecicek.tech)**

---

## ✨ Özellikler

| Alan | Ne Yapar |
|------|----------|
| 🏠 **Ev Yönetimi** | Ev oluşturma, benzersiz davet kodu üretme, bilgileri güncelleme |
| 👥 **Üye Sistemi** | Üye davet etme/çıkarma, `ADMIN` / `MEMBER` rolü atama, rol tabanlı erişim kontrolü |
| 🔑 **Davet Kodları** | `HOUSE-XXXXXX` formatında paylaşılabilir kodlarla anında ev katılımı |
| 📅 **Etkinlik Takvimi** | Renkli kategorilerle tam CRUD destekli ev takvimi |
| 🛒 **Ev İhtiyaçları** | `BEKLEMEDE` / `TAMAMLANDI` statüsüyle ortak alışveriş ve görev listesi takibi |
| 💳 **İşlemler** | Kategorili gelir/gider kaydı |
| 🔐 **Güvenlik** | JWT (JSON Web Token) kimlik doğrulama, BCrypt parola şifreleme, Spring Security, IDOR koruması, CORS yapılandırması |
| 📖 **API Dokümantasyonu** | SpringDoc OpenAPI ile etkileşimli Swagger UI |

---

## 🛠 Teknoloji Yığını

### Backend
| Teknoloji | Versiyon | Amaç |
|-----------|----------|------|
| Java | 17 | Programlama dili |
| Spring Boot | 4.1.0 | Uygulama çatısı |
| Spring Security | — | Kimlik doğrulama ve yetkilendirme |
| JJWT (Java JWT) | 0.12.5 | Stateless JWT token üretimi ve doğrulaması |
| Spring Data JPA | — | ORM / repository katmanı |
| Hibernate | 7.4.5 | JPA implementasyonu |
| PostgreSQL | 16 | İlişkisel veritabanı |
| Lombok | — | Tekrarlayan kod azaltma |
| SpringDoc OpenAPI | 2.5.0 | Swagger UI / API dökümanı |
| JUnit 5 + Mockito | — | Birim testleri |

### Frontend
| Teknoloji | Versiyon | Amaç |
|-----------|----------|------|
| React | 19 | UI kütüphanesi |
| Vite | 8 | Build aracı ve geliştirme sunucusu |
| React Router | 7 | İstemci taraflı yönlendirme |
| Axios | 1.19 | HTTP istemcisi |
| Vanilla CSS | — | Stil (framework şişkinliği yok) |

### Altyapı
| Teknoloji | Amaç |
|-----------|------|
| Docker + Docker Compose | Konteynerize yerel ve üretim ortamı |
| GitHub Actions | CI/CD pipeline (derleme → test → deploy) |
| GHCR (GitHub Container Registry) | Docker imaj depolama |
| VPS (SSH deploy) | Üretim sunucusu |

---

## 🏗 Mimari

Backend, sorumlulukların net biçimde ayrıldığı katmanlı bir mimari üzerine inşa edilmiştir:

```
com.nestify/
├── api/              # REST Controller'lar (HTTP katmanı)
│   ├── HouseController.java
│   ├── EventController.java
│   ├── UserController.java
│   └── ...
├── business/         # Servis arayüzleri + implementasyonlar (Manager deseni)
│   ├── HouseService.java       ← arayüz
│   ├── HouseManager.java       ← implementasyon
│   └── ...
├── dataAccess/       # Spring Data JPA Repository'leri
├── dataTransferObject/
│   ├── request/      # Gelen istek DTO'ları
│   └── response/     # Giden yanıt DTO'ları
├── entities/         # JPA Entity sınıfları
│   ├── House.java
│   ├── User.java
│   ├── HouseMember.java
│   ├── Event.java
│   ├── EventCategory.java
│   ├── HouseNeed.java
│   ├── Transaction.java
│   └── enums/        # MemberRole, NeedStatus, TransactionType, TransactionCategory
├── mapper/           # Entity ↔ DTO dönüşümleri
├── helpers/          # Ortak yardımcı servisler (getUserOrThrow, getHouseOrThrow…)
├── policies/         # İş kuralı doğrulayıcılar (rol kontrolleri, IDOR ve üyelik güvenceleri)
└── core/             # Güvenlik & altyapı: SecurityConfig, JwtService, JwtAuthenticationFilter, UserPrincipal, CorsConfig
```

### Temel Tasarım Kararları

- **Policy Nesneleri** — "Sadece ADMIN üye çıkarabilir" gibi iş kuralları ayrı `*Policy` sınıflarına taşınmıştır; bu sayede Manager sınıfları sade kalır, kurallar bağımsız olarak test edilebilir.
- **Helper Servisler** — Tekrarlayan `findById / orElseThrow` kalıpları, tüm Manager'larda kod tekrarını önlemek için `*ServiceHelper` sınıflarında merkezileştirilmiştir.
- **DTO Deseni** — Controller'lar entity'leri hiçbir zaman doğrudan dışarıya açmaz; tüm veri akışı özel istek/yanıt DTO'ları üzerinden yürür.
- **Arayüz + Manager** — Her servisin bir arayüzü (`HouseService`) ve somut bir implementasyonu (`HouseManager`) vardır; bu yapı birim testlerde kolay mocking ve ilerleyen dönemde sınıf değişimini mümkün kılar.

---

## 🚀 Başlarken

### Gereksinimler
- Docker & Docker Compose

### Docker Compose ile Çalıştır (önerilen)

```bash
git clone https://github.com/hamdiyekaya/nestify.git
cd nestify

# Tüm servisleri başlat (PostgreSQL + Backend + Frontend)
docker compose up --build
```

| Servis | URL |
|--------|-----|
| Frontend | http://localhost |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 |

### Yerel Geliştirme Ortamı

**Backend:**
```bash
# Java 17 ve çalışan bir PostgreSQL instance gerektirir
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev      # http://localhost:5173 adresinde başlar
```

---

## 📖 API Dokümantasyonu

Etkileşimli Swagger UI şu adreste mevcuttur:

```
http://localhost:8080/swagger-ui.html
```

Temel endpoint grupları:

| Endpoint | Metot | Açıklama |
|----------|-------|----------|
| `/api/v1/auth/login` | `POST` | Kullanıcı girişi (JWT Access Token döner) |
| `/api/v1/users` | `POST` | Yeni kullanıcı kaydı |
| `/api/v1/users` | `GET` | Sayfalamalı ve sıralamalı kullanıcı listesi |
| `/api/v1/houses` | `GET / POST` | Ev listeleme ve yeni ev oluşturma |
| `/api/v1/houses/{id}` | `GET / PUT / DELETE` | Ev detay, güncelleme ve silme |
| `/api/v1/houses/{houseId}/members` | `GET / POST` | Ev üyelerini listeleme ve yeni üye ekleme |
| `/api/v1/houses/{houseId}/members/join` | `POST` | Davet koduyla eve katılma |
| `/api/v1/houses/{houseId}/members/{userId}` | `DELETE` | Evden üye çıkarma (Yetki kontrollü) |
| `/api/v1/houses/{houseId}/members/{userId}/role` | `PATCH` | Üye rolünü değiştirme (`ADMIN` / `MEMBER`) |
| `/api/v1/houses/{houseId}/events` | `GET / POST` | Eve ait etkinlikleri listeleme ve oluşturma |
| `/api/v1/houses/{houseId}/events/{eventId}` | `PUT / DELETE` | Etkinlik güncelleme ve silme (IDOR korumalı) |
| `/api/v1/houses/{houseId}/event-categories` | `GET / POST` | Renkli etkinlik kategorileri yönetimi |
| `/api/v1/houses/{houseId}/house-needs` | `GET / POST` | Ortak alışveriş ve ihtiyaç listesi takibi |
| `/api/v1/houses/{houseId}/transactions` | `GET / POST` | Gelir / gider işlemleri ve bütçe takibi |

---

## 🧪 Testler

Birim testler, servis katmanı iş mantığını tam bağımlılık mocking'iyle kapsayan **JUnit 5** ve **Mockito** kullanılarak yazılmıştır.

```bash
./mvnw test
```

Test kapsamı örnekleri:
- `UserServiceTest` — kullanıcı kaydı, ID ile sorgulama (Helper & Policy mock'lu izolasyon testi)
- Tüm servis testleri repository, helper ve mapper bağımlılıklarını izole ederek iş kurallarını bağımsız test eder

---

## 🔄 CI/CD Pipeline

Proje, `main` branch'ine her push'ta tetiklenen tam otomatik bir GitHub Actions pipeline'ına sahiptir:

```
main'e push
     │
     ├── değişiklik-tespiti       ← Akıllı: yalnızca değişen tarafı yeniden derler
     │       ├── backend?  ──── backend-derle  (mvn test → Docker build → GHCR push)
     │       └── frontend? ──── frontend-derle (Docker build → GHCR push)
     │
     └── deploy                   ← VPS'e SSH bağlantısı, yeni imajları çek, konteynerleri yeniden başlat
```

**Pipeline öne çıkan özellikler:**
- 🔍 **Değişiklik tespiti** — `dorny/paths-filter` sayesinde yalnızca frontend değiştiyse backend imajı yeniden derlenmez; tersi de geçerlidir.
- ✅ **Her deploy'dan önce testler çalışır** — Registry'ye imaj yayınlanmadan önce `mvn test` başarıyla geçmek zorundadır.
- 🐳 **GHCR'a imaj yayını** — Sürümlendirilmiş Docker imajları GitHub Container Registry'de depolanır.
- 🔒 **Kodda sıfır gizli bilgi** — Tüm kimlik bilgileri çalışma zamanında GitHub Actions Secrets aracılığıyla enjekte edilir.

---

## 📁 Proje Yapısı

```
nestify/
├── src/                        # Spring Boot backend
│   ├── main/java/com/nestify/
│   └── test/java/com/nestify/
├── frontend/                   # React + Vite frontend
│   ├── src/
│   │   ├── pages/              # CalendarPage, DashboardPage, HousesPage…
│   │   ├── components/         # EventsTab, MembersTab, HouseNeedsTab…
│   │   ├── api/                # Axios API istemci modülleri
│   │   └── context/            # React Context (global state)
│   └── Dockerfile
├── .github/workflows/
│   └── deploy.yml              # CI/CD pipeline
├── Dockerfile                  # Backend Docker imajı
├── docker-compose.yml          # Tam kapsamlı yerel ortam
└── pom.xml
```

---

## 📬 İletişim

**Hamdiye Kaya** — [hamdiyecicek.tech](https://nestify.hamdiyecicek.tech)

> *Bu proje; sistem tasarımı ve katmanlı mimariden konteynerleştirme ve otomatik deploy'a kadar gerçek dünyaya ait full-stack mühendislik becerilerimi sergilemek amacıyla portföyüm kapsamında geliştirilmiştir.*
