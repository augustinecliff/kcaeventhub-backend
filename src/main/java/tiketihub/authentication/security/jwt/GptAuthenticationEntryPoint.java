package tiketihub.authentication.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GptAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private JWTUtil jwt;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        int responseCode  = HttpServletResponse.SC_UNAUTHORIZED;
        String s = s = "Unauthorized";

        String token = request.getHeader("Authorization");
        if (token == null) token = "";
        if (!token.isEmpty()) {
            if (jwt.validateToken(token.substring(7))) {
                responseCode = HttpServletResponse.SC_FORBIDDEN;
                s = "Forbidden access";
            }
        }

        log.info(s + " error: {}", authException.getMessage());
        response.sendError(responseCode, s);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(responseCode);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", responseCode);
        body.put("error", s);
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
