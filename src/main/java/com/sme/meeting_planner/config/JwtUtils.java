package com.sme.meeting_planner.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sme.meeting_planner.model.auth.Admin;
import com.sme.meeting_planner.model.enums.Role;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {

    private final SecretKeySpec key;
    private final Instant expirationDate;

    public JwtUtils(@Value("${jwtSecret}") String secret, @Value("${jwtExpiration}") long experation) {
        this.key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        this.expirationDate = Instant.ofEpochMilli(experation);
    }

    public String createToken(Admin user) {
        Map<String, Object> map = Map.of("id", user.getId(), "roles", user.getRoles());
        return Jwts.builder().setClaims(map)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(expirationDate))
                .signWith(key)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public Admin parse(String token) {
        final var jwt = Jwts
                .parser()
                .setSigningKey(key)
                .build()
                .parse(token);
        var claims = (Claims) jwt.getBody();
        List<Role> roles = (List<Role>) claims.get("roles");
        return Admin.builder().id(Long.parseLong(claims.get("id").toString()))
                .roles((roles)).build();
    }

}
