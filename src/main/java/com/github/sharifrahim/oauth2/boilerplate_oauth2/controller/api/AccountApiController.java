package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller.api;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.ProfileProperties;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.AccountResponse;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.ProfileUpdateRequest;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountProfileService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;

@RestController
@RequestMapping("/api")
public class AccountApiController {

    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final ProfileProperties profileProperties;

    public AccountApiController(AccountService accountService, AccountProfileService accountProfileService, ProfileProperties profileProperties) {
        this.accountService = accountService;
        this.accountProfileService = accountProfileService;
        this.profileProperties = profileProperties;
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> me(Authentication authentication) {
        Account account = resolveAccount(authentication);
        AccountProfile profile = accountProfileService.findByAccountId(account.getId()).orElse(null);
        return ResponseEntity.ok(AccountResponse.from(account, profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<AccountResponse> updateProfile(@RequestBody ProfileUpdateRequest request, Authentication authentication) {
        Account account = resolveAccount(authentication);
        AccountProfile profile = accountProfileService.ensureProfile(account, null, null);

        if (profileProperties.isRequireCompletion() && !StringUtils.hasText(request.displayName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display name is required.");
        }

        profile.setDisplayName(trimToNull(request.displayName()));
        profile.setPhoneNumber(trimToNull(request.phoneNumber()));
        profile.setCompany(trimToNull(request.company()));

        accountProfileService.save(profile);
        return ResponseEntity.ok(AccountResponse.from(account, profile));
    }

    private Account resolveAccount(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof DefaultOAuth2User oauth2User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        String email = oauth2User.getAttribute("email");
        if (!StringUtils.hasText(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user does not provide an email address.");
        }

        Optional<Account> accountOpt = accountService.getAccountByEmail(email);
        return accountOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found for email: " + email));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
