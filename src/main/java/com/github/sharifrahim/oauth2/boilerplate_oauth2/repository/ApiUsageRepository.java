package com.github.sharifrahim.oauth2.boilerplate_oauth2.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.ApiUsage;

@Repository
public interface ApiUsageRepository extends JpaRepository<ApiUsage, Long> {

    @Query("SELECT COUNT(u) FROM ApiUsage u WHERE u.email = :email AND u.recordedAt > :since")
    long countByEmailSince(@Param("email") String email, @Param("since") Instant since);
}
