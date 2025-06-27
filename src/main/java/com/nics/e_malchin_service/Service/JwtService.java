//package com.nics.e_malchin_service.Service;
//
//import io.jsonwebtoken.Jwts;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class JwtService {
//
//    private final JwtDecoder jwtDecoder;
//
//    public JwtService(JwtDecoder jwtDecoder) {
//        this.jwtDecoder = jwtDecoder;
//    }
//
//    public String extractUsername(String token) {
//        Jwt jwt = jwtDecoder.decode(token);
//        return jwt.getClaimAsString("preferred_username"); // эсвэл "sub"
//    }
//
//    public boolean isTokenValid(String token) {
//        try {
//            jwtDecoder.decode(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//}
//
