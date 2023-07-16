package tiketihub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    @Bean
    public SecurityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests()
                .requestMatchers()
                .hasRole()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/home")
                .defaultSuccessUrl("")
    }
}
