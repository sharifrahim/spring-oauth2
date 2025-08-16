package com.github.sharifrahim.oauth2.boilerplate_oauth2.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.LoginAttempt;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.status = 'FAILED' AND la.createdAt > :since")
    long countByIpAddressAndStatusFailedSince(@Param("ipAddress") String ipAddress, @Param("since") Instant since);

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.email = :email AND la.status = 'FAILED' AND la.createdAt > :since")
    long countByEmailAndStatusFailedSince(@Param("email") String email, @Param("since") Instant since);

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.status = 'BLOCKED' AND la.expiresAt > :now")
    long countOfRecentBlocksByIp(@Param("ipAddress") String ipAddress, @Param("now") Instant now);

}
