🛒 Online Marketplace (Spring Boot Microservices)

A distributed online marketplace built using Spring Boot microservices, demonstrating service-oriented architecture, inter-service communication, containerization, and deployment on Minikube.

🔍 Overview

Users can register, browse products, place orders, and pay via wallet.

Three independent microservices:

Account Service → User management & discount tracking

Marketplace Service → Products & orders

Wallet Service → Balance management & payments

Services communicate via REST APIs.

Phase 1: Dockerized microservices

Phase 2: Scalable deployment on Minikube with concurrency control

🏗 Architecture
Account Service  →  Marketplace Service  →  Wallet Service
(User Data)         (Products & Orders)     (Payments)


Account manages users and discount usage

Marketplace handles products, stock, and orders

Wallet handles debits, credits, and refunds

🛠 Tech Stack

Java 17+, Spring Boot, Spring Data JPA

H2 Database (in-memory)

Maven, Docker, Minikube

⚡ Setup
# Clone repo
git clone https://github.com/Annu-1606/Spring_marketplace.git

# Build and run each service
cd account-service && mvn spring-boot:run
cd marketplace-service && mvn spring-boot:run
cd wallet-service && mvn spring-boot:run


Account → http://localhost:8080

Marketplace → http://localhost:8081

Wallet → http://localhost:8082

📡 Key APIs

POST /users → Register user

POST /orders → Place order

PUT /wallets/{id} → Debit/Credit wallet

DELETE /orders/{id} → Cancel order (refund & stock restore)

🚀 Highlights

10% discount on first order per user

Order cancellation refunds wallet balance & restores stock

Dockerized for portability

Scales on Minikube with load-balanced Marketplace replicas
