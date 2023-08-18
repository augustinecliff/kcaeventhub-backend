package tiketihub.authentication.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tiketihub.authentication.security.jwt.AuthTokenFilter;
import tiketihub.authentication.security.jwt.ForbiddenAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
@Slf4j
public class JWTSecurityConfig {
    @Autowired
    private UserDetailsServiceImp userDetailsService;
    @Autowired
    private AuthenticationEntryPoint unauthorizedEntryPointJwt;
    @Autowired
    private ForbiddenAuthenticationEntryPoint forbiddenEntryPoint;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/api/event/**").permitAll()
                                .anyRequest().authenticated()

                )
                .exceptionHandling(handler -> {
                        handler.accessDeniedHandler(forbiddenEntryPoint);
                        handler.authenticationEntryPoint(unauthorizedEntryPointJwt);
                })
        .authenticationProvider(daoAuthenticationProvider())
        .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handler -> {
                    handler.authenticationEntryPoint(unauthorizedEntryPoint());
                    handler.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
                        log.info(HttpStatus.FORBIDDEN.toString());
                    });
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()

                )

                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }*/

    /*@Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        log.info(HttpStatus.UNAUTHORIZED.toString());
        return (request, response, authException) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }*/

}
