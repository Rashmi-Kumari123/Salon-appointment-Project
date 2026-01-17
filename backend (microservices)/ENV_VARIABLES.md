# Environment Variables

This document lists all environment variables required for the Salon Booking System microservices.

## Payment Gateway Configuration

### Stripe
- `STRIPE_API_KEY`: Stripe API key (test or live)
  - Test key format: `sk_test_...`
  - Live key format: `sk_live_...`
  - Required for: `payment`, `booking`, `notifications` services

### Razorpay
- `RAZORPAY_API_KEY`: Razorpay API key
- `RAZORPAY_API_SECRET`: Razorpay API secret
  - Required for: `payment`, `booking`, `notifications` services

## Database Configuration

- `DB_PASSWORD`: Database password (default: `Rashmi@123` for local dev)
- `DB_USERNAME`: Database username (default: `root` for local dev)
- `DB_URL`: Full database connection URL

## JWT Configuration

- `JWT_SECRET`: Secret key for JWT token generation (minimum 256 bits)
  - Required for: `user-service`, `gateway-server`

## Email Configuration

- `MAIL_USERNAME`: Email username for sending notifications
- `MAIL_PASSWORD`: Email app password (not regular password)
  - Required for: All services that send emails

## Service Discovery

- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: Eureka server URL
  - Default: `http://localhost:8070/eureka/`
  - Required for: All microservices

## Kafka Configuration

- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers
  - Default: `localhost:9092`
  - Required for: Services using Kafka (booking, payment, notifications, user-service)

## Setting Environment Variables

### Local Development

Create a `.env` file in the project root (not committed to Git):

```bash
# Payment Gateway
export STRIPE_API_KEY=sk_test_your_key_here
export RAZORPAY_API_KEY=your_key_here
export RAZORPAY_API_SECRET=your_secret_here

# Database
export DB_PASSWORD=your_database_password
export DB_USERNAME=root

# JWT
export JWT_SECRET=your-256-bit-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long

# Email
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password

# Eureka
export EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8070/eureka/

# Kafka
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

Then source it:
```bash
source .env
```

### Docker Compose

Add to `docker-compose.yml` environment section:

```yaml
services:
  payment:
    environment:
      STRIPE_API_KEY: ${STRIPE_API_KEY:-}
      RAZORPAY_API_KEY: ${RAZORPAY_API_KEY:-}
      RAZORPAY_API_SECRET: ${RAZORPAY_API_SECRET:-}
      # ... other variables
```

Or use a `.env` file that Docker Compose will automatically read:
```bash
# .env file (not committed)
STRIPE_API_KEY=sk_test_your_key_here
RAZORPAY_API_KEY=your_key_here
RAZORPAY_API_SECRET=your_secret_here
```

### Production

Set environment variables on your hosting platform:

#### AWS
- Use AWS Secrets Manager or Environment Variables in ECS/EC2
- Example: `aws secretsmanager create-secret --name stripe-api-key --secret-string "sk_live_..."`

#### Heroku
```bash
heroku config:set STRIPE_API_KEY=sk_live_...
heroku config:set RAZORPAY_API_KEY=your_key
heroku config:set RAZORPAY_API_SECRET=your_secret
```

#### Kubernetes
Create a Secret:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: salon-app-secrets
type: Opaque
stringData:
  STRIPE_API_KEY: "sk_live_..."
  RAZORPAY_API_KEY: "your_key"
  RAZORPAY_API_SECRET: "your_secret"
```

Then reference in deployment:
```yaml
env:
  - name: STRIPE_API_KEY
    valueFrom:
      secretKeyRef:
        name: salon-app-secrets
        key: STRIPE_API_KEY
```

## Security Best Practices

1. **Never commit actual API keys** - Even test keys should not be in Git
2. **Use environment variables** - Secrets should come from environment
3. **Rotate keys if exposed** - If keys were committed, rotate them immediately
4. **Use different keys per environment** - Test keys for dev, live keys for production
5. **Use secrets management** - For production, use AWS Secrets Manager, HashiCorp Vault, etc.
6. **Limit key permissions** - Use keys with minimum required permissions
7. **Monitor key usage** - Set up alerts for unusual activity

## Service-Specific Variables

### User Service
- `JWT_SECRET`
- `MAIL_USERNAME`, `MAIL_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`

### Payment Service
- `STRIPE_API_KEY`
- `RAZORPAY_API_KEY`, `RAZORPAY_API_SECRET`
- `MAIL_USERNAME`, `MAIL_PASSWORD`

### Booking Service
- `STRIPE_API_KEY`
- `RAZORPAY_API_KEY`, `RAZORPAY_API_SECRET`
- `KAFKA_BOOTSTRAP_SERVERS`

### Gateway Server
- `JWT_SECRET`
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`

### All Services
- `DB_PASSWORD`, `DB_USERNAME`, `DB_URL`
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`
