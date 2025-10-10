package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.ProfileProperties;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.ProfileForm;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountProfileService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;

@Controller
public class ProfileController {

    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final ProfileProperties profileProperties;

    public ProfileController(AccountService accountService, AccountProfileService accountProfileService, ProfileProperties profileProperties) {
        this.accountService = accountService;
        this.accountProfileService = accountProfileService;
        this.profileProperties = profileProperties;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        Account account = resolveAccount(authentication);
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String fullName = oauth2User.getAttribute("name");
        String givenName = oauth2User.getAttribute("given_name");

        AccountProfile profile = accountProfileService.ensureProfile(account, fullName, givenName);
        ProfileForm form = new ProfileForm(profile.getDisplayName(), profile.getPhoneNumber(), profile.getCompany());

        model.addAttribute("form", form);
        model.addAttribute("profileRequired", profileProperties.isRequireCompletion());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("form") ProfileForm form,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        Account account = resolveAccount(authentication);
        AccountProfile profile = accountProfileService.ensureProfile(account, null, null);

        if (profileProperties.isRequireCompletion() && !StringUtils.hasText(form.getDisplayName())) {
            bindingResult.rejectValue("displayName", "displayName.required", "Display name is required.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("profileRequired", profileProperties.isRequireCompletion());
            return "profile";
        }

        profile.setDisplayName(StringUtils.hasText(form.getDisplayName()) ? form.getDisplayName() : null);
        profile.setPhoneNumber(StringUtils.hasText(form.getPhoneNumber()) ? form.getPhoneNumber() : null);
        profile.setCompany(StringUtils.hasText(form.getCompany()) ? form.getCompany() : null);

        accountProfileService.save(profile);
        redirectAttributes.addFlashAttribute("profileUpdated", true);
        return "redirect:/dashboard";
    }

    private Account resolveAccount(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof DefaultOAuth2User oauth2User)) {
            throw new IllegalStateException("Authenticated principal is not an OAuth2 user.");
        }

        String email = oauth2User.getAttribute("email");
        if (!StringUtils.hasText(email)) {
            throw new IllegalStateException("Authenticated user does not provide an email address.");
        }

        Optional<Account> accountOpt = accountService.getAccountByEmail(email);
        return accountOpt.orElseThrow(() -> new IllegalStateException("Account not found for email: " + email));
    }
}
