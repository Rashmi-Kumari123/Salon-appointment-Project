# Running and Testing Guide for Salon Booking Microservices

## Prerequisites

1. **Java 21** installed
2. **Maven 3.8+** installed
3. **Docker** and **Docker Compose** installed
4. **Postman** or **curl** for API testing

## Step 1: Build salon-proto-common Module First

The common proto module must be built before any service can use it.

```bash
cd "backend (microservices)/salon-proto-common"
mvn clean install
```

This will generate gRPC classes that all services depend on.

## Step 2: Build All Microservices

Build all services to ensure they compile correctly:

```bash
cd "backend (microservices)"

# Build each service
mvn clean install -f user-service/pom.xml
mvn clean install -f salon/pom.xml
mvn clean install -f booking/pom.xml
mvn clean install -f category/pom.xml
mvn clean install -f service-offering/pom.xml
mvn clean install -f payment/pom.xml
mvn clean install -f notifications/pom.xml
mvn clean install -f review/pom.xml
mvn clean install -f gateway-server/pom.xml
mvn clean install -f eurekaserver/pom.xml
```

Or build all at once (if you have a parent POM):
```bash
mvn clean install
```

## Step 3: Start Infrastructure Services

Start MySQL, Kafka, Zookeeper, and Eureka first:

```bash
cd "backend (microservices)/docker-compose/default"

# Start infrastructure services only
docker-compose up -d mysql zookeeper kafka eurekaserver

# Wait for services to be healthy (check logs)
docker-compose logs -f mysql
docker-compose logs -f eurekaserver
```

**Verify Infrastructure:**
- MySQL: `mysql -h localhost -u root -p` (password: Rashmi@123)
- Eureka Dashboard: http://localhost:8070
- Kafka: `docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092`

## Step 4: Start Microservices in Order

**IMPORTANT:** After Eureka Server is running, services must be started in the correct dependency order to avoid connection failures.

### Service Dependency Analysis

Based on gRPC client dependencies and Kafka requirements:

```
Eureka Server (Port 8070)
    ↓
User Service (Port 5001, gRPC 9090) - NO microservice dependencies
    ↓
Salon Service (Port 5002, gRPC 9091) - Depends on User Service
    ↓
    ├──→ Category Service (Port 5004, gRPC 9092) - Depends on User + Salon
    │       ↓
    │       └──→ Service-Offering Service (Port 5005, gRPC 9095) - Depends on User + Salon + Category
    │               ↓
    │               └──→ Booking Service (Port 5003, gRPC 9093) - Depends on User + Salon + Service-Offering + Payment
    │                       ↓
    │                       └──→ Notifications Service (Port 5007) - Depends on User + Booking
    │
    ├──→ Review Service (Port 5008) - Depends on User + Salon (can start in parallel with Category/Payment)
    │
    └──→ Payment Service (Port 5006, gRPC 9094) - Depends on User + Salon (can start in parallel with Category/Review)
            ↓
            └──→ Booking Service (Port 5003, gRPC 9093)
                    ↓
                    └──→ Notifications Service (Port 5007)
                            ↓
                            Gateway Server (Port 5000) - START LAST
```

### Detailed Startup Sequence

#### Phase 1: Infrastructure (Already Running)
1. **MySQL** - Database (port 3306)
2. **Zookeeper** - For Kafka (port 2181)
3. **Kafka** - Message broker (port 9092)
4. **Eureka Server** - Service discovery (port 8070)

#### Phase 2: Base Service (No Microservice Dependencies)
5. **User Service** (Port 5001, gRPC 9090)
   - **Dependencies**: MySQL, Eureka, Kafka
   - **Why First**: All other services depend on User Service via gRPC
   - **gRPC Server**: Yes (port 9090)
   - **Command**: `docker-compose up -d user-service`
   - **Wait**: Check health at `http://localhost:5001/actuator/health`

#### Phase 3: Level 1 Dependent Services (Depend on User Service Only)
6. **Salon Service** (Port 5002, gRPC 9091)
   - **Dependencies**: MySQL, Eureka, **User Service** (gRPC)
   - **gRPC Server**: Yes (port 9091)
   - **gRPC Clients**: User Service
   - **Command**: `docker-compose up -d salon`
   - **Wait**: Check health at `http://localhost:5002/actuator/health`

#### Phase 4: Level 2 Dependent Services (Depend on User + Salon)
7. **Category Service** (Port 5004, gRPC 9092) - Can start in parallel with Review/Payment
   - **Dependencies**: MySQL, Eureka, **User Service**, **Salon Service** (gRPC)
   - **gRPC Server**: Yes (port 9092)
   - **gRPC Clients**: User Service, Salon Service
   - **Command**: `docker-compose up -d category`

8. **Review Service** (Port 5008) - Can start in parallel with Category/Payment
   - **Dependencies**: MySQL, Eureka, **User Service**, **Salon Service** (gRPC)
   - **gRPC Server**: No
   - **gRPC Clients**: User Service, Salon Service
   - **Command**: `docker-compose up -d review`

9. **Payment Service** (Port 5006, gRPC 9094) - Can start in parallel with Category/Review
   - **Dependencies**: MySQL, Eureka, Kafka, **User Service**, **Salon Service** (gRPC)
   - **gRPC Server**: Yes (port 9094)
   - **gRPC Clients**: User Service, Salon Service
   - **Command**: `docker-compose up -d payment`

**Parallel Start Command:**
```bash
docker-compose up -d category review payment
```

#### Phase 5: Level 3 Dependent Services (Depend on User + Salon + Category)
10. **Service-Offering Service** (Port 5005, gRPC 9095)
    - **Dependencies**: MySQL, Eureka, **User Service**, **Salon Service**, **Category Service** (gRPC)
    - **gRPC Server**: Yes (port 9095)
    - **gRPC Clients**: User Service, Salon Service, Category Service
    - **Command**: `docker-compose up -d service-offering`
    - **Wait**: Must wait for Category Service to be healthy

#### Phase 6: Level 4 Dependent Services (Depend on Multiple Services)
11. **Booking Service** (Port 5003, gRPC 9093)
    - **Dependencies**: MySQL, Eureka, Kafka, **User Service**, **Salon Service**, **Service-Offering Service**, **Payment Service** (gRPC)
    - **gRPC Server**: Yes (port 9093)
    - **gRPC Clients**: User Service, Salon Service, Service-Offering Service, Payment Service
    - **Command**: `docker-compose up -d booking`
    - **Wait**: Must wait for Service-Offering AND Payment Services to be healthy

#### Phase 7: Level 5 Dependent Services (Depend on Booking)
12. **Notifications Service** (Port 5007)
    - **Dependencies**: MySQL, Eureka, Kafka, **User Service**, **Booking Service** (gRPC)
    - **gRPC Server**: No
    - **gRPC Clients**: User Service, Booking Service
    - **Command**: `docker-compose up -d notifications`
    - **Wait**: Must wait for Booking Service to be healthy

#### Phase 8: Gateway (Start Last)
13. **Gateway Server** (Port 5000)
    - **Dependencies**: Eureka (waits for all services to register)
    - **Why Last**: Should start after all services are registered in Eureka for proper routing
    - **Command**: `docker-compose up -d gateway-server`
    - **Wait**: Verify all services appear in Eureka Dashboard first

### Quick Start Commands

#### Option 1: Sequential (Safest - Recommended for First Time)
```bash
cd "backend (microservices)/docker-compose/default"

# After Eureka is running and healthy
docker-compose up -d user-service
sleep 10  # Wait for user-service to be healthy

docker-compose up -d salon
sleep 10  # Wait for salon to be healthy

docker-compose up -d category review payment  # Can start in parallel
sleep 15  # Wait for all to be healthy

docker-compose up -d service-offering
sleep 10  # Wait for service-offering to be healthy

docker-compose up -d booking
sleep 10  # Wait for booking to be healthy

docker-compose up -d notifications
sleep 10  # Wait for notifications to be healthy

docker-compose up -d gateway-server
```

#### Option 2: Using Docker Compose Dependencies (Recommended)
```bash
# Docker Compose handles dependencies automatically based on depends_on configuration
cd "backend (microservices)/docker-compose/default"
docker-compose up -d
```

Docker Compose will:
- Start services in dependency order automatically
- Wait for health checks before starting dependent services
- Handle all the complexity for you

### Key Points to Remember

1. **User Service MUST start first** - All services depend on it via gRPC
2. **Salon Service second** - Many services depend on it
3. **Category, Review, Payment can start in parallel** after Salon is healthy
4. **Service-Offering needs Category** - Must wait for Category Service
5. **Booking needs Service-Offering AND Payment** - Must wait for both
6. **Notifications needs Booking** - Must wait for Booking Service
7. **Gateway should start last** - After all services register with Eureka

### Verification After Each Phase

After starting each phase, verify services are healthy:

```bash
# Check service health
curl http://localhost:5001/actuator/health  # user-service
curl http://localhost:5002/actuator/health  # salon
curl http://localhost:5004/actuator/health  # category
curl http://localhost:5005/actuator/health  # service-offering
curl http://localhost:5006/actuator/health  # payment
curl http://localhost:5003/actuator/health  # booking
curl http://localhost:5007/actuator/health  # notifications
curl http://localhost:5008/actuator/health  # review
curl http://localhost:5000/actuator/health  # gateway
```

**Check Eureka Dashboard:**
- Open: http://localhost:8070
- Verify each service appears as registered instance
- All services should show "UP" status before starting Gateway

## Step 5: Verify Services are Running

```bash
# Check all containers
docker-compose ps

# Check service health
curl http://localhost:5001/actuator/health  # user-service
curl http://localhost:5002/actuator/health  # salon
curl http://localhost:5003/actuator/health  # booking
curl http://localhost:5004/actuator/health  # category
curl http://localhost:5005/actuator/health  # service-offering
curl http://localhost:5006/actuator/health  # payment
curl http://localhost:5007/actuator/health  # notifications
curl http://localhost:5008/actuator/health  # review
curl http://localhost:5000/actuator/health  # gateway
```

**Check Eureka Dashboard:**
- Open: http://localhost:8070
- All services should appear as registered instances

## Step 6: API Testing Service by Service

### Service 1: User Service (Port 5001)

**Base URL:** `http://localhost:5001` or via Gateway: `http://localhost:5000`

#### 1.1 Sign Up
```bash
POST http://localhost:5000/auth/signup
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```

#### 1.2 Login
```bash
POST http://localhost:5000/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:** Save the `token` from response for subsequent requests.

#### 1.3 Get User Profile
```bash
GET http://localhost:5000/api/users/profile
Authorization: Bearer <token>
```

---

### Service 2: Salon Service (Port 5002)

**Base URL:** `http://localhost:5002` or via Gateway: `http://localhost:5000`

#### 2.1 Create Salon (as OWNER)
```bash
POST http://localhost:5000/api/salons
Authorization: Bearer <owner_token>
Content-Type: application/json

{
  "name": "Hair Studio",
  "address": "123 Main St",
  "phoneNumber": "1234567890",
  "email": "salon@example.com",
  "city": "Mumbai",
  "openTime": "09:00:00",
  "closeTime": "18:00:00"
}
```

#### 2.2 Get All Salons
```bash
GET http://localhost:5000/api/salons
```

#### 2.3 Get Salon by ID
```bash
GET http://localhost:5000/api/salons/{salonId}
```

---

### Service 3: Category Service (Port 5004)

**Base URL:** `http://localhost:5004` or via Gateway: `http://localhost:5000`

#### 3.1 Create Category
```bash
POST http://localhost:5000/api/categories
Authorization: Bearer <owner_token>
Content-Type: application/json

{
  "name": "Haircut",
  "image": "https://example.com/image.jpg",
  "salonId": 1
}
```

#### 3.2 Get All Categories
```bash
GET http://localhost:5000/api/categories
```

#### 3.3 Get Categories by Salon
```bash
GET http://localhost:5000/api/categories/salon/{salonId}
Authorization: Bearer <token>
```

---

### Service 4: Service-Offering Service (Port 5005)

**Base URL:** `http://localhost:5005` or via Gateway: `http://localhost:5000`

#### 4.1 Create Service Offering
```bash
POST http://localhost:5000/api/service-offering
Authorization: Bearer <owner_token>
Content-Type: application/json

{
  "name": "Men's Haircut",
  "description": "Professional men's haircut",
  "price": 500,
  "duration": 30,
  "salonId": 1,
  "categoryId": 1,
  "available": true
}
```

#### 4.2 Get Services by Salon
```bash
GET http://localhost:5000/api/service-offering/salon/{salonId}
```

#### 4.3 Get Service by ID
```bash
GET http://localhost:5000/api/service-offering/{serviceId}
```

---

### Service 5: Booking Service (Port 5003)

**Base URL:** `http://localhost:5003` or via Gateway: `http://localhost:5000`

#### 5.1 Create Booking
```bash
POST http://localhost:5000/api/bookings?salonId=1&paymentMethod=RAZORPAY
Authorization: Bearer <customer_token>
Content-Type: application/json

{
  "startTime": "2024-01-20T10:00:00",
  "endTime": "2024-01-20T10:30:00",
  "serviceIds": [1, 2]
}
```

**Response:** Returns payment link URL.

#### 5.2 Get Customer Bookings
```bash
GET http://localhost:5000/api/bookings/customer
Authorization: Bearer <customer_token>
```

#### 5.3 Get Salon Bookings
```bash
GET http://localhost:5000/api/bookings/report?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <owner_token>
```

---

### Service 6: Payment Service (Port 5006)

**Base URL:** `http://localhost:5006` or via Gateway: `http://localhost:5000`

#### 6.1 Create Payment Link
```bash
POST http://localhost:5000/api/payments/create?paymentMethod=RAZORPAY
Authorization: Bearer <customer_token>
Content-Type: application/json

{
  "id": 1,
  "salonId": 1,
  "customerId": 1,
  "startTime": "2024-01-20T10:00:00",
  "endTime": "2024-01-20T10:30:00",
  "serviceIds": [1, 2],
  "status": "PENDING",
  "totalPrice": 1000
}
```

#### 6.2 Get Payment Order
```bash
GET http://localhost:5000/api/payments/{paymentOrderId}
```

---

### Service 7: Review Service (Port 5008)

**Base URL:** `http://localhost:5008` or via Gateway: `http://localhost:5000`

#### 7.1 Create Review
```bash
POST http://localhost:5000/api/reviews
Authorization: Bearer <customer_token>
Content-Type: application/json

{
  "salonId": 1,
  "rating": 5,
  "comment": "Great service!"
}
```

#### 7.2 Get Reviews by Salon
```bash
GET http://localhost:5000/api/reviews/salon/{salonId}
```

---

### Service 8: Notifications Service (Port 5007)

**Base URL:** `http://localhost:5007` or via Gateway: `http://localhost:5000`

#### 8.1 Get User Notifications
```bash
GET http://localhost:5000/api/notifications/user
Authorization: Bearer <token>
```

#### 8.2 Get Salon Notifications
```bash
GET http://localhost:5000/api/notifications/salon-owner/salon/{salonId}
Authorization: Bearer <owner_token>
```

---

## Testing via Gateway (Recommended)

All services are accessible through the API Gateway at `http://localhost:5000`. The gateway handles:
- Service discovery via Eureka
- JWT authentication
- CORS
- Request routing

**Example:**
```bash
# Instead of: http://localhost:5001/auth/login
# Use: http://localhost:5000/auth/login
```

## Troubleshooting

### Service Not Starting
```bash
# Check logs
docker-compose logs <service-name>

# Common issues:
# 1. Database connection failed - check MySQL is running
# 2. Eureka connection failed - check Eureka is running
# 3. gRPC port conflict - check ports are not in use
# 4. Kafka connection failed - check Kafka is running
```

### Service Not Registered in Eureka
```bash
# Check Eureka dashboard: http://localhost:8070
# Verify service name matches Eureka registration
# Check network connectivity between services
```

### gRPC Connection Issues
```bash
# Verify gRPC ports are exposed
docker-compose ps

# Test gRPC connection
grpcurl -plaintext localhost:9090 list
```

### Database Connection Issues
```bash
# Test MySQL connection
mysql -h localhost -u root -p
# Password: Rashmi@123

# Check database exists
SHOW DATABASES;
USE salon_db;
```

## Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop specific service
docker-compose stop <service-name>
```

## Useful Commands

```bash
# View logs
docker-compose logs -f <service-name>

# Restart service
docker-compose restart <service-name>

# Check service status
docker-compose ps

# Execute command in container
docker-compose exec <service-name> /bin/bash
```

## Postman Collection

Create a Postman collection with:
1. Environment variables:
   - `base_url`: `http://localhost:5000`
   - `token`: (set after login)
   - `owner_token`: (set after owner login)

2. Pre-request scripts to set Authorization header:
   ```javascript
   pm.request.headers.add({
       key: 'Authorization',
       value: 'Bearer ' + pm.environment.get('token')
   });
   ```

3. Tests to save token:
   ```javascript
   if (pm.response.code === 200) {
       var jsonData = pm.response.json();
       pm.environment.set("token", jsonData.data.token);
   }
   ```
