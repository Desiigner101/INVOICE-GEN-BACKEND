# Invoice Gen - Backend

REST API for the Invoice Gen invoice management system, built with Spring Boot and MongoDB.

---

## Tech Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 17
- **Database:** MongoDB 6.x (Spring Data MongoDB)
- **Authentication:** Clerk (JWT verification)
- **Email:** JavaMailSender via SMTP
- **Cloud Storage:** Cloudinary
- **Build Tool:** Maven

---

## Prerequisites

- Java JDK 17+
- Maven 3.6+
- MongoDB 6.x or a MongoDB Atlas account
- Clerk account (for JWT verification)
- Cloudinary account (for template image storage)
- SMTP credentials (Gmail or SendGrid)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Desiigner101/INVOICE-GEN.git
cd INVOICE-GEN/invoice-generator-backend
```

### 2. Configure application properties

Create or edit `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/invoicedb

clerk.secret-key=your_clerk_secret_key

cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

The server will start at `http://localhost:8080`.

---

## Project Structure

```
src/
└── main/
    ├── java/com/invoicegen/
    │   ├── controller/       # REST controllers
    │   ├── service/          # Business logic
    │   ├── repository/       # MongoDB data access
    │   ├── model/            # Document models
    │   └── config/           # App configuration (Clerk, Cloudinary, etc.)
    └── resources/
        └── application.properties
```

---

## API Reference

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

All endpoints require a valid Clerk JWT in the `Authorization` header:

```
Authorization: Bearer <jwt_token>
```

### Endpoints

#### Invoices

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/invoices` | Get all invoices for the authenticated user |
| GET | `/api/v1/invoices/{id}` | Get a single invoice by ID |
| POST | `/api/v1/invoices` | Create a new invoice |
| PUT | `/api/v1/invoices/{id}` | Update an existing invoice |
| DELETE | `/api/v1/invoices/{id}` | Delete an invoice |
| POST | `/api/v1/invoices/{id}/send` | Send invoice to client via email |

#### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users/profile` | Get the authenticated user's profile |
| POST | `/api/v1/users/sync` | Sync user data from Clerk webhook |

### Example Request Body (Create Invoice)

```json
{
  "clientName": "John Doe",
  "clientEmail": "john@example.com",
  "invoiceItems": [
    {
      "description": "Web Design Services",
      "quantity": 1,
      "unitPrice": 1500.00
    }
  ],
  "totalAmount": 1500.00,
  "issueDate": "2026-02-01",
  "dueDate": "2026-03-01",
  "status": "draft"
}
```

---

## Testing

```bash
# Unit tests
mvn test

# Integration tests
mvn verify
```

---

## Deployment

### Railway

```bash
npm i -g @railway/cli
railway login
railway init
railway up
```

Set your environment variables in the Railway dashboard to match the values in `application.properties`.

### MongoDB Atlas

1. Create a cluster at [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas)
2. Whitelist your server IP
3. Copy the connection string and update `spring.data.mongodb.uri`

---

## Author

**Sarsonas, Kervin Gino M.**
- GitHub: [@Desiigner101](https://github.com/Desiigner101)
- Email: kervingino.sarsonas@cit.edu
