# Spring Boot OAuth2 Identity Module - Retro Terminal Edition

This project transforms a basic Spring Boot OAuth2 demo into a production-ready identity module with a unique retro terminal UI. It provides a solid foundation for handling user accounts, authentication, account status tracking, and security features like rate limiting.

## Features

- **OAuth2 Authentication**: Secure login with Google.
- **Account Status Tracking**: Each OAuth sign-in provisions or reuses an `Account` with an `ACTIVE`/`SUSPENDED` status flag.
- **Profile Management**: Optional one-to-one `AccountProfile` records capture display name, phone, and company details.
- **Rate Limiting**: Protects against brute-force attacks by limiting login attempts.
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

This project uses environment variables to handle sensitive credentials. Create a `.env` file in the project root (or set the variables in your deployment environment). You can use the `.env.example` file as a template.

```bash
# .env.example
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/oauth2
SPRING_DATASOURCE_USERNAME=oauth2
SPRING_DATASOURCE_PASSWORD=oauth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

### 2. Getting OAuth2 Credentials

You will need to create an OAuth2 application for Google:

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
- **Profile Enforcement**:
  - `profile.require-completion`: When `true`, users are redirected to `/profile` after OAuth login until a display name is provided.

## Running the Application

Once the environment variables are set, you can run the application using Maven:

```sh
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Database Inspection

This application uses PostgreSQL backed by Flyway migrations. You can run a Postgres instance locally with Docker:

```bash
docker run --rm -d \
  --name oauth2-db \
  -e POSTGRES_DB=oauth2 \
  -e POSTGRES_USER=oauth2 \
  -e POSTGRES_PASSWORD=oauth2 \
  -p 5432:5432 \
  postgres:16-alpine
```

Once the service is up:

```bash
psql "postgresql://oauth2:oauth2@localhost:5432/oauth2"
```

Useful tables:

- `accounts`: user accounts with status flags.
- `account_profiles`: optional profile metadata.
- `oauth_providers`: linked OAuth providers.
- `login_attempts`: rate limiting audit trail.

Flyway migrations live in `src/main/resources/db/migration`. Add new schema changes as incremental `V__` scripts and Flyway will run them automatically on startup.
