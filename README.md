
# Core Microservices

This project is a modular Java-based microservices architecture built for teaching purposes, demonstrating how to structure and deploy microservices using Spring Boot .

## 🎯 Purpose

The goal is to provide a practical example for students and junior developers to understand the fundamentals of microservices, including service registration, communication, API gateways, and security.

## 🧩 Architecture Overview

The system includes the following services:

- **BalanceService**: Manages account balances.
- **CustomerService**: Manages customer data.
- **DepositService**: Handles deposit transactions.
- **WithdrawalService**: Handles withdrawals.
- **TransactionService**: Logs all transactions.
- **SecurityService**: Provides JWT-based authentication and authorization.
- **api-gateway**: Routes requests to microservices.
- **discovery-server**: Eureka-based service registry.

## 🛠️ Technologies Used

- Java 17
- Spring Boot
- Spring Cloud
- Eureka (Netflix OSS)
- Spring Security with JWT
- Spring Cloud Gateway
- Maven
- Docker

## 📂 Project Structure

```
coremicroservices/
├── api-gateway/
├── balance-service/
├── customer-service/
├── deposit-service/
├── discovery-server/
├── security-service/
├── transaction-service/
├── withdrawal-service/
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 17
- Maven
- Docker (optional, for containerization)

### Clone the Repository

```bash
git clone https://github.com/seifeda/coremicroservices.git
cd coremicroservices
```

### Build Services

```bash
mvn clean install
```

### Run Services Individually

Start the discovery server first:

```bash
cd discovery-server
mvn spring-boot:run
```

Then, start other services in separate terminals.

### Run with Docker

```bash
docker build -t [service-name] ./[service-directory]
docker run -d -p [host-port]:[container-port] [service-name]
```

## 🔐 Security

- JWT tokens are issued by the SecurityService.
- Include the token in the `Authorization` header as a Bearer token in all requests.

## 🧪 Testing

```bash
mvn test
```

## 🤝 Contributing

Feel free to fork this repository and contribute by submitting pull requests.



## 📬 Contact

For questions, reach out at: seifebekele07@gmail.com
