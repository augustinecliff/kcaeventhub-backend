package tiketihub.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tiketihub.user.UserSession;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long exprationTime;

    private Key jwtKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String generateJwtAuthenticationToken(Authentication authentication) {
        UserSession userPrincipal = (UserSession) authentication.getPrincipal();

        Date expirationDate = new Date(new Date().getTime() + exprationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userPrincipal.getEmail());
        log.info("\nUser to be authenticated has the email : ("+userPrincipal.getEmail()+")");

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(jwtKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String generatePasswordResetToken(String email, Date exprationDate) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(exprationDate)
                .signWith(jwtKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtKey()).build().parse(token);
            return true;
        } catch (Exception e) {
            log.error("JWT Error: {}", e.getMessage());
            return false;
        }
    }
}
