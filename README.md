# FoodFlow

A backend-heavy food delivery platform (Swiggy/Zomato-style) built with Java 21 + Spring Boot.
Focus: real backend architecture and business logic — order lifecycle, delivery assignment, ETA
calculation, surge pricing, coupon engine, ratings, notifications, analytics — not just CRUD.

## Tech Stack
Java 21 · Spring Boot 3 · Spring Security (JWT) · Spring Data JPA · MySQL · Maven · Swagger/OpenAPI · JUnit 5 · Mockito

## Features
- **Auth**: JWT-based, role-based access (Customer, Restaurant Owner, Delivery Agent, Admin)
- **Restaurant & Menu**: categories, food items, ingredient-based inventory
- **Cart & Orders**: single-restaurant cart, snapshot-based order items, full order lifecycle state machine
- **Delivery Assignment**: nearest-available-agent algorithm using Haversine distance
- **ETA Calculation**: prep time + distance/speed with traffic multiplier
- **Surge Pricing**: dynamic delivery charge based on active-order-to-online-agent ratio
- **Coupons**: min bill, max discount cap, expiry, usage limits, first-order-only, restaurant-specific
- **Payments**: simulated (UPI/Card/COD/Wallet), auto-refund on cancellation
- **Ratings & Reviews**: restaurant/food/delivery-agent ratings with auto-updating averages
- **Notifications**: stored per order-status milestone
- **Analytics**: admin-level platform overview, restaurant-level revenue/top-items/daily-sales

## Setup
1. Create/confirm a local MySQL instance is running (the app auto-creates the `foodflow_db` schema)
2. Update `src/main/resources/application.yml` with your MySQL username/password
3. Run `FoodflowApplication.java` from VS Code (Spring Boot Dashboard) or `mvn spring-boot:run`
4. API docs: `http://localhost:8080/swagger-ui.html` — click **Authorize** and paste a JWT (from `/api/auth/login`) to test protected endpoints directly in the browser

## Running Tests
VS Code → Testing panel (left sidebar) → Run All

## Architecture Notes
- **Delivery assignment** uses the Haversine formula for straight-line distance rather than a
  real routing API — a deliberate MVP tradeoff (cost/complexity vs. accuracy) worth discussing.
- **Order state transitions** are enforced via an explicit `Map<OrderStatus, Set<OrderStatus>>`
  in `OrderService` — a single source of truth, not scattered if/else checks.
- **Analytics aggregation** happens in the service layer over fetched lists rather than raw SQL
  `GROUP BY` — fine at MVP data volume, would move to SQL/native queries at scale.

## Progress Log
- [x] Project setup
- [x] Auth (register/login, JWT, role-based access)
- [x] Restaurant & menu management
- [x] Cart & order lifecycle
- [x] Delivery assignment, ETA, surge pricing
- [x] Coupons & payments
- [x] Ratings, reviews, notifications
- [x] Analytics, tests, documentation

**MVP complete.** Possible future additions: Redis caching, Docker, WebSocket live tracking, email notifications, CI/CD.