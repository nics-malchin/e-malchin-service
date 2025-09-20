package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Bah;
import com.nics.e_malchin_service.Entity.Horshoo;
import com.nics.e_malchin_service.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    @Autowired
    UserDAO userDAO;
    @Autowired
    BahService bahService;
    @Autowired
    HorshooService horshooService;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    public User registerUser(User user, String roleName) {
        // 1. Админ access_token авах
        String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "admin-cli");
        params.add("username", adminUsername);
        params.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. Keycloak-д хэрэглэгч үүсгэх
        String createUserUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";
        headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> kcUser = new HashMap<>();
        kcUser.put("username", user.getUsername());
        kcUser.put("enabled", true);

        Map<String, Object> creds = new HashMap<>();
        creds.put("type", "password");
        creds.put("value", user.getPassword());
        creds.put("temporary", false);

        kcUser.put("credentials", new Object[]{creds});
        kcUser.put("firstName", user.getFirstName());
        kcUser.put("lastName", user.getLastName());

        HttpEntity<Map<String, Object>> kcRequest = new HttpEntity<>(kcUser, headers);
        restTemplate.postForEntity(createUserUrl, kcRequest, String.class);

        // 3. Өөрийн DB-д хадгалах

        switch (roleName.toLowerCase()) {
            case "bah" -> {
                Bah bah = new Bah();
                bah.setName(user.getFirstName() + " " + user.getLastName());
                bah.setUsername(user.getUsername());
                bah.setPassword(user.getPassword());
                bah.setHorshoo_id(user.getHorshoo_id());
                bah.setCreatedBy(1000);
                bahService.addBah(bah);
            }
            case "horshoo" -> {
                Horshoo horshoo = new Horshoo();
                horshoo.setName(user.getFirstName() + " " + user.getLastName());
                horshoo.setUsername(user.getUsername());
                horshoo.setPassword(user.getPassword());
                horshoo.setCreatedBy(1000);
                horshooService.addHorshoo(horshoo);
            }
            case "malchin" -> {
                user.setCreatedBy(1000);
                userDAO.save(user);
            }
            default -> throw new RuntimeException("Unknown role: " + roleName);
        }

        return user;
    }
}
