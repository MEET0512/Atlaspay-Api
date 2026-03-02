Overview

AtlasPay API is a secure RESTful backend application built using Spring Boot. It provides user authentication and transaction tracking functionality with JWT-based security and role-based authorization.

The system ensures secure session management and reliable transactional operations for financial data handling.

Tech Stack

Java

Spring Boot

Spring Security

JWT Authentication

Spring Data JPA

MySQL

Maven

JUnit

Postman

Features

User registration and login

JWT-based authentication

Role-based access control

CRUD operations for transactions

Secure endpoint protection

Transactional database operations

Layered architecture (Controller → Service → Repository)

API Endpoints

POST /auth/register

POST /auth/login

GET /transactions

POST /transactions

PUT /transactions/{id}

DELETE /transactions/{id}

Security

Access Token & Refresh Token support

Password encryption

Protected routes using Spring Security filters

How to Run

Clone repository

Configure database in application.properties

Run using Maven:
mvn spring-boot:run

Test endpoints using Postman
