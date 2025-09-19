package com.nics.e_malchin_service.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String pin;
    private Integer bahId;
    private Integer horshooId;
    private Integer phoneNumber;
    private Integer isLicenseApproved;
    private Integer familyId;
    private Integer aimagId;
    private Integer sumId;
    private Integer bagId;
    private Integer locationDescription;
    private Integer herderCount;
    private Integer familyCount;
    private String organizationName;
}

