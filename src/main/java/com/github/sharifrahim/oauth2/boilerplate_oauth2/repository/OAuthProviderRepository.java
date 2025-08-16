package com.github.sharifrahim.oauth2.boilerplate_oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.OAuthProvider;

@Repository
public interface OAuthProviderRepository extends JpaRepository<OAuthProvider, Long> {
}
