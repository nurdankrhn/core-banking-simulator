# Core Banking Simulator
Core Banking Simulator is a backend-focused banking system built with Java 17, Spring Boot 3, PostgreSQL, Kafka.
It simulates essential banking operations such as:

- Customer onboarding & authentication

- Account creation & management

- Money transfers (internal/external)

- Fraud detection through Kafka-based workflows

- Notification service

- End-of-Day (EOD) processing

- Transaction history and limits

- JWT + Refresh Token security

This project is designed to mimic real-world backend architecture suitable for portfolio or educational use.

## Features
### Customer Management

- Customer registration

- JWT-secure login

- Refresh token mechanism

- Role-based access control (USER, ADMIN)

### Account Management

- Checking accounts

- Auto IBAN creation

- Balance retrieval

- Account status management

- Daily transfer limit functionality

### Transaction Operations

- Transfers, deposits, withdrawals

- Transaction validation

- Ledger posting

- Kafka-based event propagation

- Fraud Detection

- Kafka consumer for fraud detection

- Suspicious activity checks

- Fraud alert storage

- Admin review interface

### Notification Service

- Kafka-based notifications

- Fraud, success, failure messages

- End-of-Day (EOD)

- Interest calculation

- Batch closure

- Fraud batch analysis

### Tech Stack
- Backend

- Java 17

- Spring Boot 

- Spring Security

- Spring Data JPA

- Infrastructure

- PostgreSQL

- Apache Kafka

### Tools

- Docker

- Postman

### Project Structure
src/main/java/CoreBankingSimulator<br>
 ├── config<br>
 ├── controller<br>
 ├── dto<br>
 ├── exceptions<br>
 ├── model<br>
 ├── repository<br>
 ├── security<br>
 ├── services<br>
 └── util<br>

## Database ER Diagram
       ┌──────────────────────────┐
       │        customers         │
       │──────────────────────────│
       │ id (PK)                  │
       │ email                    │
       │ first_name               │
       │ last_name                │
       │ password                 │
       └───────────┬──────────────┘
                   │ 1
                   │
                   │ N
       ┌───────────▼──────────────┐
       │      customer_roles      │
       │──────────────────────────│
       │ customer_id (FK)         │
       │ role                     │
       └──────────────────────────┘

       ┌──────────────────────────┐
       │        accounts          │
       │──────────────────────────│
       │ id (PK)                  │
       │ account_type             │
       │ balance                  │
       │ created_at               │
       │ iban                     │
       │ status                   │
       │ daily_limit              │
       │ daily_transferred_amount│
       │ min_balance             │
       │ customer_id (FK)───────────┘
       └───────────┬──────────────┘

                   │ 1
                   │
                   │ N
       ┌───────────▼──────────────┐
       │       transactions        │
       │──────────────────────────│
       │ id (PK)                  │
       │ amount                   │
       │ created_at               │
       │ description              │
       │ direction                │
       │ reference_iban           │
       │ type                     │
       │ account_id (FK)          │
       │ created_by (FK)          │
       │ currency                 │
       │ reference_id             │
       │ status                   │
       │ updated_at               │
       └───────────┬──────────────┘

                   │ 1
                   │
                   │ N
       ┌───────────▼──────────────┐
       │      fraud_alerts        │
       │──────────────────────────│
       │ id (PK)                  │
       │ alert_type               │
       │ created_at               │
       │ reviewed_by_admin        │
       │ account_id (FK)          │
       │ transaction_id (FK)      │
       └──────────────────────────┘

       ┌──────────────────────────┐
       │      notifications       │
       │──────────────────────────│
       │ id (PK)                  │
       │ customer_id (FK)         │
       │ message                  │
       │ type                     │
       └──────────────────────────┘

       ┌──────────────────────────┐
       │     refresh_tokens       │
       │──────────────────────────│
       │ id (PK)                  │
       │ token                    │
       │ expiry                   │
       │ customer_id (FK)         │
       └──────────────────────────┘

## API Endpoints

Below is the complete API reference for the Core Banking Simulator.<br>

### Customer & Authentication Endpoints
| Feature | Method | Endpoint | Auth Required| Request Body | Response |
|------------|-----------|--------|-------------------|--------------------|--------------------|
| Register Customer (USER) | POST | /api/customers/register | No | ```{ "firstName":"Nurdan","lastName":"Karahann","email":"nurdan@example.com","password":"123456" }``` | Returns created customer |
| Register Admin User | POST | /api/customers/register | No | ```{ "firstName":"NurdanAdmin","lastName":"Karahan","email":"nurdanadmin@example.com","password":"123456a","roles":["ADMIN"] }``` | Roles include ADMIN |
| Login | POST | /api/auth/login | No | ```{ "email":"nurdan@example.com","password":"123456" }``` | Returns access & refresh tokens |
| Get Profile | GET | /api/customers/me | Yes| ```Header: Bearer <token>``` | Customer details |
| Admin Dashboard Test | GET | /api/customers/admin/dashboard | ADMIN Only	| - | 403 when normal USER |


### Account Endpoints
| Feature | Method | Endpoint | Auth Required| Request | Response |
|------------|-----------|--------|-------------------|--------------------|--------------------|
| Create Account | POST | /api/accounts | Yes | ```{ "customerId":1,"accountType":"CHECKING" }``` | Account with IBAN |
| List My Accounts | GET | /api/accounts/my | Yes | - | Array of accounts |
| Account Balance | GET | /api/accounts/{id}/balance | Yes | - | Returns balance |
| Change Account Status | PUT | /api/accounts/{id}/status?status=FROZEN | ADMIN | - | Updated account |
| List All Accounts | GET | /api/accounts/my | Yes | - | Accounts list |

### Transaction Endpoints (Kafka-Integrated)
| Feature | Method | Endpoint | Auth Required| Query Params | Response |
|------------|-----------|--------|-------------------|--------------------|--------------------|
| Transfer | POST | /transactions/transfer/{id} | Yes | ```	targetIban, amount, description``` | Posted transaction |
| Withdraw | POST | /transactions/withdraw/{id} | Yes | ```	amount``` | Posted transaction |
| Deposit | POST | /transactions/deposit/{id} | Yes | ```	amount, description``` | Posted transaction |


##  Screenshots
- Admin Area Access
  <img width="1920" height="1025" alt="adminAreaAccess4Admin" src="https://github.com/user-attachments/assets/57d14d9b-69e3-4359-8b2d-376c47d11aac" />

- Change Status Of Account
 <img width="1920" height="1026" alt="changeStatusOfAccounts" src="https://github.com/user-attachments/assets/d8c810fc-3b4b-44d3-a47d-7407281e0622" />

- Create Bank Account  
<img width="1916" height="1027" alt="createBankAccountExample" src="https://github.com/user-attachments/assets/08a2d72c-a2cf-4615-867f-8674fb585c54" />

- Deposite Process
  <img width="1920" height="1030" alt="deposite" src="https://github.com/user-attachments/assets/bbb94c01-b39a-43b8-be3f-3e8ed95ce20b" />
  <img width="1295" height="168" alt="depositeDB" src="https://github.com/user-attachments/assets/9629af94-f521-4bf3-9a8f-8234570e932c" />

- Transfer Process
<img width="1920" height="1032" alt="transferProcess" src="https://github.com/user-attachments/assets/09071bb8-4a71-4f07-af79-af65fcacce79" />  
<img width="1337" height="152" alt="transactionDB" src="https://github.com/user-attachments/assets/2430ea72-27fd-471a-ba5e-74dec3442cf3" />
- Transfer Process on Kafka Side    
<img width="1813" height="653" alt="transactionKAFKA" src="https://github.com/user-attachments/assets/123847b1-a7e8-428e-9ad0-04a7125f2249" />

- Withdraw Process
<img width="1920" height="1032" alt="withdraw" src="https://github.com/user-attachments/assets/7c8d62b4-a576-484e-87b6-65573d1916b5" />
<img width="1228" height="181" alt="withdrawDB" src="https://github.com/user-attachments/assets/20de050c-da9a-4676-85b3-875597fdb2fe" />

  
- JWT Auth Checking
  <img width="1443" height="972" alt="jwtAuthCheck" src="https://github.com/user-attachments/assets/ab40ad58-9b46-4bf4-8323-fff029bfa1e6" />

- Fraud Alert Example  
<img width="1920" height="1027" alt="fraudAlert" src="https://github.com/user-attachments/assets/fd190138-fbb7-43b8-a667-d8736004c469" />

- Notification Service Log  
<img width="1903" height="538" alt="noficationServiceLog" src="https://github.com/user-attachments/assets/de21460f-6985-4c19-8d8c-2cc4b39dd093" />


## How to Run the Project
1. Clone
git clone https://github.com/.../CoreBankingSimulator.git
cd CoreBankingSimulator

2. Configure PostgreSQL & docker-compose.yml
3. Start Kafka & Redis (Docker recommended)
4. Run Application
mvn spring-boot:run
