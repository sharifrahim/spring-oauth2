package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyDataDto {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;

    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position cannot exceed 100 characters")
    private String position;

    @Min(value = 1, message = "Company size must be at least 1")
    private int companySize;

    @Size(max = 100, message = "Industry cannot exceed 100 characters")
    private String industry;

    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getCompanySize() {
        return companySize;
    }

    public void setCompanySize(int companySize) {
        this.companySize = companySize;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
