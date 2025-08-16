# Spring Boot OAuth2 Identity Module - Retro Terminal Edition

This project transforms a basic Spring Boot OAuth2 demo into a full-featured, production-ready identity management system with a unique retro terminal UI. It provides a complete foundation for handling user accounts, authentication, multi-step registration, and security features like rate limiting.

## Features

- **OAuth2 Authentication**: Secure login with GitHub, Google, and GitLab.
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
GITLAB_CLIENT_ID=your_gitlab_client_id
GITLAB_CLIENT_SECRET=your_gitlab_client_secret
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

- **GitLab**:
  1. Go to `User Settings > Applications`.
  2. Create a new application.
  3. Set the `Redirect URI` to `http://localhost:8080/login/oauth2/code/gitlab`.
  4. Ensure the application has the `read_user`, `openid`, `profile`, and `email` scopes.

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
