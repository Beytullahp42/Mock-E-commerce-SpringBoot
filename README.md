# Mock E-Commerce API

Spring Boot API for my mock e-commerce portfolio project.

I originally wrote this application as the final project for the **SE-016 Web Applications** course at the Estonian Entrepreneurship University of Applied Sciences during my Erasmus exchange in the **2024-2025 spring semester**. The preserved, polished school submission is available on the [`course-final-2025`](https://github.com/Beytullahp42/Mock-E-commerce-SpringBoot/tree/course-final-2025) branch. The `master` branch contains the post-course portfolio improvements.

## Live demo

- Customer application: https://mock-ecommerce.beytullahp.com
- Admin panel: https://mock-ecommerce.beytullahp.com/admin
- API: https://mec-api.beytullahp.com

The public demo is reset every day. Accounts, orders, products, and admin changes may disappear.

Demo administrator:

- Email: `admin@admin.com`
- Password: `password123`

## Portfolio v2 additions

- Stateless Spring Security with one-hour JWTs and BCrypt password hashes
- Customer registration and login using email
- `ROLE_USER` and `ROLE_ADMIN` authorization
- One reusable cart per account and customer-owned order history
- Admin-protected product, upload, order, and status-management endpoints
- Hard product deletion with removal from active carts
- Immutable order-item snapshots, so completed orders retain their original product name, description, price, quantity, and image URL after later catalog edits or deletion
- Historical image retention: an old physical image is removed only when no completed order snapshot references its filename
- Exact environment-controlled CORS origins

JWTs are deliberately returned to the React client and stored in `localStorage`. This small portfolio project does not use refresh tokens or authentication cookies. Missing, invalid, expired, or deleted-account tokens return `401`; valid users without the required role receive `403`.

## Main API routes

Public:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/items` and `GET /api/items/{id}`
- `GET /upload-dir/{filename}`

Authenticated:

- `GET /api/auth/me`
- `/api/cart/**`
- `POST /api/orders`
- `GET /api/orders` and `GET /api/orders/{id}`

Administrator:

- `/api/admin/items/**`
- `/api/admin/orders/**`
- `/api/admin/uploads/**`

All unlisted routes are denied by default. CSRF is intentionally disabled because the API is stateless and accepts bearer tokens rather than authentication cookies.

## Run with Docker Compose

This repository's Compose file builds both the API and the sibling React repository from their current local source code. It does not pull prebuilt application images from Docker Hub.

Requirements:

- Docker with Compose
- This repository and `Mock-E-commerce-React` checked out beside each other

```bash
cp .env.example .env
docker compose up -d --build
```

Default local addresses:

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- PostgreSQL: `localhost:5432`

Set a new base64-encoded secret of at least 32 bytes in `JWT_SECRET` before a public deployment. `CORS_ALLOWED_ORIGINS` is a comma-separated exact allowlist. `VITE_BASE_URL` is compiled into the Vite production bundle during the Docker build.

The v2 account/cart/order ownership model is intended for a fresh database. Compose never deletes or replaces the existing `pgdata` volume automatically.

Frontend repository: https://github.com/Beytullahp42/Mock-E-commerce-React
