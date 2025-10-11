# Rate Limiting System

## Two-Tier Rate Limiting

### 1. Login Rate Limiting (Simple Cooldown)
- **Purpose**: Prevent brute force login attacks
- **Scope**: IP-based only
- **Limits**: 
  - 5 failed login attempts per email → cooldown
  - 10 failed attempts per IP → 60 minute block
- **Configuration**: `application.yml`

### 2. API Rate Limiting (SaaS-Ready)
- **Purpose**: Control API usage per user/tenant
- **Scope**: Email-based (per authenticated user)
- **Limits**: Per-minute, per-hour, per-day
- **Default**: Max plan (unlimited) - just records usage
- **Applied to**: All authenticated controller endpoints

## How It Works

```
┌─────────────────────────────────────────────────────────┐
│ Login Attempt                                           │
│ ├─ RateLimitFilter checks IP blocks                    │
│ └─ After 5 fails → cooldown (simple)                   │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ API Request (authenticated)                             │
│ ├─ ApiRateLimitInterceptor checks email policy         │
│ ├─ Records usage in api_usage table                    │
│ └─ Checks: per-minute, per-hour, per-day limits        │
└─────────────────────────────────────────────────────────┘
```

## Database Schema

### rate_limit_policies
```sql
email                      VARCHAR(255) UNIQUE
max_requests_per_minute    INTEGER
max_requests_per_hour      INTEGER
max_requests_per_day       INTEGER
enabled                    BOOLEAN
expires_at                 TIMESTAMP
```

### api_usage
```sql
email          VARCHAR(255)
endpoint       VARCHAR(500)
http_method    VARCHAR(10)
ip_address     VARCHAR(45)
recorded_at    TIMESTAMP
```

## Usage

### Current State: Max Plan (No Limits)
All users get unlimited API access. System just records usage.

### Create Custom Policy
```java
@Autowired
private RateLimitPolicyService rateLimitPolicyService;

// Free tier: 60/min, 1000/hour, 10000/day
rateLimitPolicyService.createOrUpdatePolicy(
    "free@example.com", 60, 1000, 10000
);

// Pro tier: 600/min, 10000/hour, 100000/day
rateLimitPolicyService.createOrUpdatePolicy(
    "pro@example.com", 600, 10000, 100000
);

// Enterprise: unlimited
rateLimitPolicyService.createOrUpdatePolicy(
    "enterprise@example.com", 
    Integer.MAX_VALUE, 
    Integer.MAX_VALUE, 
    Integer.MAX_VALUE
);
```

### Disable Policy
```java
rateLimitPolicyService.disablePolicy("user@example.com");
// Falls back to max plan (unlimited)
```

## SaaS Extension

### Add Tenant Support
```sql
ALTER TABLE rate_limit_policies ADD COLUMN tenant_id BIGINT;
ALTER TABLE api_usage ADD COLUMN tenant_id BIGINT;
```

### Lookup Hierarchy
1. Check user-specific policy
2. Fall back to tenant default policy
3. Fall back to global max plan

### Example Tiers
```java
// Starter: 100 req/min
createOrUpdatePolicy(email, 100, 5000, 50000);

// Growth: 500 req/min
createOrUpdatePolicy(email, 500, 20000, 200000);

// Enterprise: unlimited
createOrUpdatePolicy(email, MAX_VALUE, MAX_VALUE, MAX_VALUE);
```

## Monitoring

Query usage patterns:
```sql
-- Top API consumers
SELECT email, COUNT(*) as requests
FROM api_usage
WHERE recorded_at > NOW() - INTERVAL '1 hour'
GROUP BY email
ORDER BY requests DESC;

-- Endpoint popularity
SELECT endpoint, COUNT(*) as hits
FROM api_usage
WHERE recorded_at > NOW() - INTERVAL '1 day'
GROUP BY endpoint
ORDER BY hits DESC;
```
