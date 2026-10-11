# Hotel Booking App

A full-stack hotel booking application: **Spring Boot 4 REST API + MongoDB Atlas + JWT security with roles + React frontend**, deployed to **AWS (S3 + API Gateway + Lambda)**.

**Live demo:** http://student-rohan-krishna-bandla-hotel-frontend.s3-website-us-east-1.amazonaws.com

Guests register, search available rooms and book them. Admins manage rooms and guests and see every booking.

## Features

**Business rules**
- No double booking: a room can't have two active bookings on the same night
- Check-in can't be in the past; check-out must be after check-in; max 30 nights
- Total price = nights × price per night (BigDecimal)
- Bookings can be cancelled until check-in; cancelled bookings free the room
- Rooms/guests with active bookings can't be deleted
- Availability search by dates and room type

**Security**
- Register/login with BCrypt-hashed passwords and a JWT (role inside the token)
- Two roles: **ADMIN** (manages rooms and guests, sees all bookings) and **GUEST** (books for themselves, sees only own bookings)
- 401 for missing/invalid token, 403 for wrong role or someone else's booking
- Only one built-in `admin` account; nobody can register as admin

**Frontend (React + Vite)**
- Login/register, role-aware tabs and pages, one `DataService.js` for all API calls
- Loading, empty and error states; server error messages shown in the UI

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4, Spring Web MVC, Spring Data MongoDB, Spring Security, Bean Validation |
| Auth | JWT (JJWT 0.12), BCrypt |
| Database | MongoDB Atlas |
| Tests | JUnit 5, Mockito |
| Frontend | React, Vite |
| Cloud | AWS S3 (static website), API Gateway HTTP API, AWS Lambda (aws-serverless-java-container) |

## Architecture

```
Browser → S3 website (React) → API Gateway (HTTP API) → Lambda (Spring Boot) → MongoDB Atlas
```

Inside the app every request goes **JwtAuthFilter → Controller → Service → Repository**.

## Project steps (Git branches)

| Branch | Step |
|---|---|
| `Step1-rest-api-no-db` | Controller → Service → Repository, in-memory, business rules, unit tests |
| `Step2-mongodb-atlas` | MongoDB Atlas with Spring Data repositories |
| `Step3-jwt-auth` | Register/login, BCrypt, JWT, protected endpoints |
| `Step4-react-frontend` | React frontend |
| `Step5-roles-deploy` | ADMIN/GUEST roles, 403 rules, AWS deployment |

## Run locally

**Backend** – environment variables (IntelliJ → Run → Edit Configurations):

| Variable | Required | Example |
|---|---|---|
| `MONGODB_URI` | yes | `mongodb+srv://user:password@cluster.xxxxx.mongodb.net/` |
| `JWT_SECRET` | recommended | random text, 32+ characters |
| `ADMIN_PASSWORD` | recommended | password for the built-in `admin` user |

Run `HotelApiApplication` → http://localhost:8080

**Frontend**

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 (Vite proxies `/api` to port 8080).

## API

| Method | Endpoint | Who |
|---|---|---|
| POST | `/api/auth/register` | everyone (creates a GUEST) |
| POST | `/api/auth/login` | everyone → `token`, `role`, `guestId` |
| GET | `/api/rooms`, `/api/rooms/{id}`, `/api/rooms/available?checkIn&checkOut&type` | logged in |
| POST / PUT / DELETE | `/api/rooms/...` | ADMIN |
| GET / POST / PUT / DELETE | `/api/guests/...` | ADMIN |
| GET | `/api/bookings` | ADMIN: all, GUEST: own |
| POST | `/api/bookings` | GUEST books for self, ADMIN sends `guestId` |
| POST | `/api/bookings/{id}/cancel` | ADMIN, or the GUEST who owns it |

**Status codes:** 200, 201, 204 · 400 rule/validation error · 401 no/invalid token · 403 wrong role or not your booking · 404 not found · 409 username taken.

## Tests

```bash
mvn test
```

17 tests: booking rules (price, double booking, back-to-back, past dates, cancel, availability), roles (guest books only for self, sees only own bookings, can't cancel others'), JWT (role inside token, tampered/expired/foreign tokens rejected).

## Deploy to AWS

1. `mvn clean package -P lambda -DskipTests` → `target/hotel-api-0.0.1-SNAPSHOT-lambda.zip`
2. Lambda (Java 17), handler `com.example.hotel_api.StreamLambdaHandler::handleRequest`, 2048 MB, 1 min timeout, env vars `MONGODB_URI`, `JWT_SECRET`, `ADMIN_PASSWORD`
3. API Gateway HTTP API, route `ANY /{proxy+}` → Lambda, CORS for `content-type, authorization`
4. `VITE_API_BASE_URL=<invoke URL> npm run build` → upload `dist/` to an S3 bucket with static website hosting
5. MongoDB Atlas network access: `0.0.0.0/0` (Lambda has no fixed IP)