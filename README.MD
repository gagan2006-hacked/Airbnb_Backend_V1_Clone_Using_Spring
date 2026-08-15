# Airbnb V1

An Airbnb-inspired hotel booking backend built with **Spring Boot**, implementing hotel discovery, booking management, authentication, payment processing, dynamic pricing, and scheduled background jobs.

## Features

* 🏨 **Hotel Discovery & Search**

  * Retrieve hotels and search for hotels based on available criteria.

* 📅 **Booking Management**

  * Create and manage hotel bookings.
  * Scheduled job to automatically clean up incomplete/expired bookings.

* 💳 **Payment System**

  * Demo payment flow using **Razorpay**.
  * Webhook integration for handling payment lifecycle events such as:

    * Payment Created
    * Payment Captured
    * Payment Failed
    * Payment Refunded

* 💰 **Dynamic Pricing Engine**

  * Pricing is implemented using the **Strategy Design Pattern**.
  * Uses the **Decorator Design Pattern** to dynamically enhance pricing rules.
  * Includes **holiday-based dynamic pricing**, where prices are increased during holidays based on data retrieved from an external Holiday API.

* 🔐 **Authentication & Security**

  * JWT-based authentication.
  * Short-lived **Access Tokens** (~10 minutes).
  * Long-lived **Refresh Tokens** (configurable for months).
  * CSRF protection using Spring Security's `CookieCsrfTokenRepository`.
  * Protected API routes using Spring Security.

* 🗄️ **PostgreSQL**

  * PostgreSQL used as the primary relational database.
  * Hibernate/JPA for persistence and database interaction.

* ⏰ **Scheduled Background Processing**

  * Background scheduler handles cleanup of bookings that remain incomplete beyond their allowed time window.

## Design & Architecture

The project focuses not only on implementing features but also on applying software engineering principles and design patterns to keep the backend extensible.

### Design Patterns Used

* **Strategy Pattern** — Different pricing strategies can be selected without modifying the core pricing logic.
* **Decorator Pattern** — Additional pricing rules such as holiday surcharges can be layered dynamically over an existing pricing strategy.

## Tech Stack

**Backend**

* Java
* Spring Boot
* Spring Security
* Spring Data JPA / Hibernate

**Database**

* PostgreSQL

**Authentication**

* JWT
* Access & Refresh Tokens
* CSRF Protection

**Payments**

* Razorpay
* Webhooks

**External APIs**

* Holiday API

**Architecture Concepts**

* Strategy Pattern
* Decorator Pattern
* Scheduled Jobs
* REST APIs

> **V1 Scope:** This version focuses on the core hotel discovery, booking, payment, authentication, pricing, and background-processing workflows. Features such as friendships/social functionality and cloud-drive support for bookings are planned for future versions.
