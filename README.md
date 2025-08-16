# Spring Boot OAuth2 Identity Module - Retro Terminal Edition

This project transforms a basic Spring Boot OAuth2 demo into a full-featured, production-ready identity management system with a unique retro terminal UI. It provides a complete foundation for handling user accounts, authentication, multi-step registration, and security features like rate limiting.

## Features

- **OAuth2 Authentication**: Secure login with GitHub and Google.
- **Complete Account Lifecycle**: Manages user accounts from creation to cleanup.
- **Multi-Step Registration**: A customizable, state machine-driven registration flow for new users.
- **Rate Limiting**: Protects against brute-force attacks by limiting login and registration attempts.
- **Scheduled Cleanup**: Automatically deletes abandoned, pending accounts to maintain database hygiene.
- **Customizable Error Handling**: A full suite of themed error pages for a consistent user experience.
- **Unique Retro UI**: A nostalgic terminal/DOS-style user interface built with custom CSS.

## The Retro Terminal UI

The entire frontend has been rebuilt from the ground up to mimic the look and feel of a classic green-on-black computer terminal.

- **Monospace Fonts & Classic Colors**: For that authentic old-school vibe.
- **ASCII Art**: Borders, logos, and success/failure messages are rendered in ASCII.
- **Blinking Cursor & CRT Effects**: Subtle animations enhance the retro experience.
- **Command-Style Interface**: Buttons and links are styled to look like terminal commands (e.g., `[ENTER] > SUBMIT`).

## Setup and Configuration

### 1. Environment Variables

This project uses environment variables to handle sensitive OAuth2 credentials. Create a `.env` file in the project root (or set the variables in your deployment environment). You can use the `.env.example` file as a template.

```bash
# .env.example
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

### 2. Getting OAuth2 Credentials

You will need to create an OAuth2 application for each provider you want to support.

- **GitHub**:
  1. Go to `Settings > Developer settings > OAuth Apps`.
  2. Create a new OAuth App.
  3. Set the `Authorization callback URL` to `http://localhost:8080/login/oauth2/code/github`.

- **Google**:
  1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
  2. Create a new project and go to `APIs & Services > Credentials`.
  3. Create an `OAuth 2.0 Client ID`.
  4. Add `http://localhost:8080` to `Authorized JavaScript origins`.
  5. Add `http://localhost:8080/login/oauth2/code/google` to `Authorized redirect URIs`.


### 3. Configuration Options

You can customize the application's behavior in `src/main/resources/application.yml`:

- **Rate Limiting**:
  - `security.rate-limiting.enabled`: `true` or `false`.
  - `security.rate-limiting.ip-max-attempts`: Max failed attempts per IP.
  - `security.rate-limiting.email-max-attempts`: Max failed attempts per email.
  - `security.rate-limiting.block-duration-in-minutes`: How long an IP is blocked.

- **Account Cleanup**:
  - `cleanup.pending-accounts.enabled`: `true` or `false`.
  - `cleanup.pending-accounts.schedule`: Cron expression for the cleanup job (default is 2 AM daily).

## Running the Application

Once the environment variables are set, you can run the application using Maven:

```sh
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Database Inspection

This application uses an in-memory H2 database for development. You can inspect the data created during OAuth registration and user flows using the H2 Console.

### Accessing H2 Console

1. **Start the application**: `mvn spring-boot:run`
2. **Open H2 Console**: Navigate to `http://localhost:8080/h2-console`
3. **Connection Details**:
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `sa`
   - **Password**: (leave empty)
   - **Driver Class**: `org.h2.Driver`

### Database Tables

The application creates several tables to manage the OAuth2 identity system:

- **`ACCOUNT`**: User accounts with email, username, status, and timestamps
- **`OAUTH_PROVIDER`**: Links accounts to OAuth providers (Google, GitHub)
- **`REGISTRATION_SESSION`**: Tracks multi-step registration progress and state
- **`LOGIN_ATTEMPT`**: Rate limiting and security audit trail

### Viewing OAuth Data

After completing an OAuth login flow, you can run these SQL queries in the H2 Console:

```sql
-- View all accounts
SELECT * FROM ACCOUNT;

-- View OAuth provider connections (GitHub, Google)
SELECT a.EMAIL, a.USERNAME, op.PROVIDER, op.PROVIDER_USER_ID 
FROM ACCOUNT a 
JOIN OAUTH_PROVIDER op ON a.ID = op.ACCOUNT_ID;

-- View registration sessions
SELECT a.EMAIL, rs.CURRENT_STATE, rs.SESSION_TOKEN, rs.EXPIRES_AT
FROM ACCOUNT a 
JOIN REGISTRATION_SESSION rs ON a.ID = rs.ACCOUNT_ID;

-- View login attempts and rate limiting
SELECT * FROM LOGIN_ATTEMPT ORDER BY CREATED_AT DESC;
```

### Environment Variables for H2 Console

You can control H2 Console access via environment variables:

- **`H2_CONSOLE_ENABLED`**: Set to `true` to enable, `false` to disable (default: `true`)
- **`SECURITY_LOG_LEVEL`**: Set logging level for security events (`INFO`, `DEBUG`, `TRACE`)
