# Spring Boot OAuth Login Demo

This project demonstrates how to implement OAuth2 login with GitHub and Google in a Spring Boot application using encrypted client ID and client secret values with Jasypt.

---

## Getting Started

### Prerequisites

1. **JDK 17**: Ensure Java Development Kit 17 or later is installed.
2. **Maven**: Ensure Maven is installed.
3. **Environment Variables**: Set up the Jasypt encryption password in your environment.
   
   ```bash
   export JASYPT_ENCRYPTOR_PASSWORD=your-encryption-password
   ```

---

### How to Obtain Client ID and Secret Key

#### 1. GitHub

1. Go to the [GitHub Developer Applications page](https://github.com/settings/developers).
2. Click **"New OAuth App"**.
3. Fill in the required fields:
   - Application name
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. After saving, you will see the **Client ID** and **Client Secret**. Save them securely.

#### 2. Google

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Navigate to **"APIs & Services" > "Credentials"**.
4. Click **"Create Credentials" > "OAuth client ID"**.
5. Configure the consent screen if required.
6. Set the application type to **Web application**.
7. Add the redirect URI: `http://localhost:8080/login/oauth2/code/google`.
8. Save to get the **Client ID** and **Client Secret**.

---

### Encrypting Client ID and Secret with Jasypt

Follow this [official Jasypt encryption guide](https://www.jasypt.org/cli.html) to encrypt your Client ID and Client Secret.

After encrypting the values, update the `application.yml` file under the `client-id` and `client-secret` fields for both GitHub and Google with the encrypted values:

```yaml
client-id: ENC(encrypted-client-id)
client-secret: ENC(encrypted-client-secret)
```

---

### Running the Application

1. **Clone the Repository**:
   
   ```bash
   git clone https://github.com/sharifrahim/spring-oauth2.git
   cd oauth-login-demo
   ```

2. **Run the Application**:

   Use Maven to run the application:

   ```bash
   mvn spring-boot:run
   ```

3. **Access the Application**:

   Open your browser and navigate to:

   ```
   http://localhost:8080
   ```

---

### Logging

For debugging OAuth login, the logging level for `org.springframework.security` is set to `TRACE`. You can find detailed logs in the console output.

---

### Notes

- Make sure your GitHub and Google OAuth applications are properly set up to redirect to `http://localhost:8080/login/oauth2/code/{provider}`.
- Use a secure password for `JASYPT_ENCRYPTOR_PASSWORD` and avoid committing it to version control.

---

### Troubleshooting

1. **Invalid Credentials**: Double-check the encrypted values and ensure the correct Jasypt password is set.
2. **Redirection Errors**: Verify the OAuth redirect URIs in the GitHub and Google developer console.

For further assistance, consult the official Spring Security OAuth2 documentation: https://spring.io/projects/spring-security

