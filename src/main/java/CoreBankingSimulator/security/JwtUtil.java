package CoreBankingSimulator.security;

import CoreBankingSimulator.services.CustomerDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long accessTokenValidity = 1000 * 60 * 15; // 15 mins
    private final long refreshTokenValidity = 1000 * 60 * 60 * 24; // 24 hours

    public String generateAccessToken(CustomerDetails userDetails) {
        Set<String> roles = userDetails.getRoles(); // e.g., ["USER"] or ["ADMIN"]

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles) // Add roles as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(CustomerDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roles", List.class);
    }


    public boolean validateToken(String token, CustomerDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}

