# 🛒 Online Marketplace (Spring Boot Microservices)

A distributed **online marketplace** built using **Spring Boot microservices**, demonstrating service-oriented architecture, inter-service communication, containerization, and deployment on **Minikube**.  

---

## 🔍 Overview
- Users can **register**, **browse products**, **place orders**, and **pay via wallet**.  
- Three independent microservices:  
  1. **Account Service** → User management & discount tracking  
  2. **Marketplace Service** → Products & orders  
  3. **Wallet Service** → Balance management & payments  
- Services communicate via **REST APIs**.  
- **Phase 1**: Dockerized microservices  
- **Phase 2**: Scalable deployment on Minikube with concurrency control  

---

## 🏗 Architecture
Account Service → Marketplace Service → Wallet Service
(User Data) (Products & Orders) (Payments)

Copy code

- **Account Service** → manages users and discount usage  
- **Marketplace Service** → handles products, stock, and orders  
- **Wallet Service** → manages debits, credits, and refunds  

---

## 🛠 Tech Stack
- Java 17+  
- Spring Boot, Spring Data JPA  
- H2 Database (in-memory)  
- Maven, Docker, Minikube  

---

## ⚡ Setup

### Clone the repo
```bash
git clone https://github.com/Annu-1606/Spring_marketplace.git
cd Spring_marketplace
Build & Run each service
bash
Copy code
# Account Service
cd account-service
mvn spring-boot:run

# Marketplace Service
cd marketplace-service
mvn spring-boot:run

# Wallet Service
cd wallet-service
mvn spring-boot:run
Default Ports
Account → http://localhost:8080

Marketplace → http://localhost:8081

Wallet → http://localhost:8082

📡 Key APIs
Account Service
POST /users → Register user

Marketplace Service
POST /orders → Place order

DELETE /orders/{id} → Cancel order (refund & stock restore)

Wallet Service
PUT /wallets/{id} → Debit/Credit wallet


