package com.nics.e_malchin_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class KeycloakUserDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
}
