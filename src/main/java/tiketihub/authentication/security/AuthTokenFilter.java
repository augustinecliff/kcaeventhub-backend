package tiketihub.authentication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tiketihub.authentication.exceptions.BlackListedTokenException;
import tiketihub.authentication.exceptions.InvalidTokenException;
import tiketihub.authentication.security.dto.JwtDTO;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && !(jwtUtil.isTokenBlackListed(jwt))) {
                if (jwtUtil.validateToken(jwt)) {
                    JwtDTO user = jwtUtil.getUserIdAndEmailFromToken(jwt);

                    String email = (jwtUtil.getUserIdAndEmailFromToken(jwt)).getEmail();
                    logger.info("\nUser email in token filter = " + email + " userId: " + user.getUserId());

                    // Check if the authentication is already set
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {

                    throw new InvalidTokenException("The token used is invalid");
                }
            } else {
                if (jwt == null) throw new InvalidTokenException("No token has been found");
                throw new BlackListedTokenException("The token has been invalidated via logout");
            }
        } catch (Exception e) {
            Object errorMessage = "Cannot set user authentication: " + e.getMessage();
            logger.error(errorMessage);
        }

        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return  headerAuth.substring(7);
        }

        return null;
    }
}
