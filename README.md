# FoodFlow

A backend-heavy food delivery platform (Swiggy/Zomato-style) built with Java 21 + Spring Boot.
Focus: real backend architecture and business logic — order lifecycle, delivery assignment, ETA
calculation, surge pricing, coupon engine — not just CRUD.

## Tech Stack
Java 21 · Spring Boot 3 · Spring Security (JWT) · Spring Data JPA · MySQL · Maven · Swagger/OpenAPI

## Status
🚧 In progress. Current phase: **Auth & Roles**.

## Setup
1. Create a MySQL database (or let `createDatabaseIfNotExist=true` handle it) and update
   `src/main/resources/application.yml` with your local MySQL username/password.
2. `mvn spring-boot:run`
3. API docs: `http://localhost:8080/swagger-ui.html`

## Progress Log
- [x] Project setup
- [x] Auth (register/login, JWT, role-based access)
- [ ] Restaurant & menu management
- [ ] Cart & order lifecycle
- [ ] Delivery assignment, ETA, surge pricing
- [ ] Coupons & payments
- [ ] Ratings, reviews, notifications
- [ ] Analytics
- [ ] Tests & documentation
