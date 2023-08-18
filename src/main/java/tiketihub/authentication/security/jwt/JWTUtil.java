package tiketihub.authentication.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tiketihub.authentication.security.dto.JwtDTO;
import tiketihub.user.UserSession;

import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key jwtKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }
    Map<String,Object> claims = new HashMap<>();

    public String generateJwtAuthenticationToken(Authentication authentication) {
        UserSession userPrincipal = (UserSession) authentication.getPrincipal();

        Date expirationDate = new Date(new Date().getTime() + expirationTime);

        /*Map<String, Object> claims = new HashMap<>();
        claims.put("email", userPrincipal.getEmail());*/
        log.info("\nUser to be authenticated has the email : ("+userPrincipal.getEmail()+")");
        claims = new HashMap<>();
        claims.put("userId", userPrincipal.getUserId());
        claims.put("email", userPrincipal.getEmail());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(jwtKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /*.setClaims(claims)*/// (can't use both setClaims & setSubject')

    public JwtDTO getUserIdAndEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtKey()).build()
                .parseClaimsJws(token).getBody();

        String userId = claims.get("userId", String.class);
        String email = claims.get("email", String.class);

        return new JwtDTO(userId, email);
    }


    public String generatePasswordConfigToken(JwtDTO jwtDTO, Date exprationDate) {
        claims = new HashMap<>();
        claims.put("userId",jwtDTO.getUserId());
        claims.put("email",jwtDTO.getEmail());
        return Jwts.builder()
                .setClaims(claims)
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

    private final Set<String> blacklistedTokens = new HashSet<>();
    public void BlackListToken(String token) {
        token = token.replace("Bearer ", "");
        blacklistedTokens.add(token);
    }
    public boolean isTokenBlackListed(String token) {
        token = token.replace("Bearer ", "");
        return blacklistedTokens.contains(token);
    }
}
