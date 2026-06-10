package com.nics.e_malchin_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuConfigDto {
    private String roleName;
    private List<String> menuKeys;
}
