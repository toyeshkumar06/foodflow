# FoodFlow — Refinements & Polishing Log

This document tracks the post-MVP polishing work done on top of the core 7-phase backend
and 7-phase frontend build. Each entry below corresponds to a `polish/*` branch and a
`vX.X-polish-*` git tag.

## P1 — Restaurant Data Overhaul
**Branch:** `polish/restaurant-data-overhaul`

- Added `imageUrl` field to `Restaurant` entity + DTOs + service, with a new
  `PATCH /api/restaurant-owner/restaurants/{id}/image` endpoint
- Replaced generic test restaurants with 6 fully branded, distinct-cuisine restaurants:
  Kyoto Nights (Japanese), Seoul Garden (Korean), Dragon Wok (Chinese),
  Punjab Tadka (North Indian), Malabar Spice (South Indian), Golden Bun Diner (American)
- Each restaurant seeded with 10-12 menu items across 2 categories, via
  `seed-restaurants.ps1` (data-driven PowerShell script)
- Images are stock placeholder photos (Picsum, seeded for consistency) — not real
  per-dish photography, since that requires an upload/storage system (potential future add)
- Frontend: restaurant cards and menu pages now render real images; customer browse
  page filters to only show `OPEN` restaurants (closed/test ones hidden, not deleted)
- Avoided using real trademarked brand names (e.g. McDonald's) for IP-safety reasons —
  used original fictional branding instead

## P2 — (planned) Welcome / landing page with rotating slogans
## P3 — (folded into P1) Food & restaurant images
## P4 — (planned) Profile avatars for all roles
## P5 — (planned) Elaborate payment method UI
## P6 — (planned) Restaurant filters & sorting
## P7 — (planned) Visual polish — Owner / Agent / Admin dashboards
## P8 — (planned) One-click local launcher
## P9 — (planned) Live deployment
