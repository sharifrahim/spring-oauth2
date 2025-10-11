CREATE TABLE rate_limit_policies (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    max_requests_per_minute INTEGER NOT NULL,
    max_requests_per_hour INTEGER NOT NULL,
    max_requests_per_day INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE TABLE api_usage (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    ip_address VARCHAR(45),
    response_status INTEGER,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_rate_limit_policies_email ON rate_limit_policies(email);
CREATE INDEX idx_api_usage_email_recorded ON api_usage(email, recorded_at);
