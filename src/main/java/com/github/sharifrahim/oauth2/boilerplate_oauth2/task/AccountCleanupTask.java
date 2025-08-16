package com.github.sharifrahim.oauth2.boilerplate_oauth2.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;

@Component
public class AccountCleanupTask {

    private final AccountService accountService;

    public AccountCleanupTask(AccountService accountService) {
        this.accountService = accountService;
    }

    @Scheduled(cron = "${cleanup.pending-accounts.schedule:0 0 2 * * ?}", zone = "UTC")
    public void cleanupPendingAccounts() {
        accountService.deleteExpiredPendingAccounts();
    }
}
