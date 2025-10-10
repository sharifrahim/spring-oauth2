package com.github.sharifrahim.oauth2.boilerplate_oauth2;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

@SpringBootTest
class BoilerplateOauth2ApplicationTests {

	private static final EmbeddedPostgres POSTGRES = startPostgres();

	private static EmbeddedPostgres startPostgres() {
		try {
			return EmbeddedPostgres.builder().start();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to start embedded Postgres", e);
		}
	}

	@DynamicPropertySource
	static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl("postgres", "postgres"));
		registry.add("spring.datasource.username", () -> "postgres");
		registry.add("spring.datasource.password", () -> "postgres");
	}

	@Test
	void contextLoads() {
	}
}
