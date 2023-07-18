package tiketihub.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static tiketihub.controller.baseMappingConfig.base;

@Configuration
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetails;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(encoder());
        authProvider.setUserDetailsService(userDetails);

        return authProvider;
    }
    @Bean
    public UserDetailsService userDetails() {
        UserDetails developerUser =
                User.withUsername("developer")
                        .password(encoder().encode("password"))
                        .roles("DEV")
                        .build();

        return new InMemoryUserDetailsManager(developerUser);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*.authorizeHttpRequests(authorize -> authorize.
                        requestMatchers(base+"/home").permitAll()
                        .requestMatchers(base+"/createAccount").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers(base+"/details").authenticated()
                        .anyRequest().authenticated()
                )*/
                .authorizeHttpRequests()
                .requestMatchers("/").permitAll()
                .requestMatchers(base+"/home").permitAll()
                .requestMatchers(base+"/createAccount").permitAll()
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/logout/**").permitAll()
                .requestMatchers(base+"/details").authenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl(base+"/details",true).permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl(base+"/home");
                /*.formLogin(form ->
                        form.loginPage("/login")
                                .loginProcessingUrl("/login").permitAll()
                                .defaultSuccessUrl("/details",true).permitAll())
                .logout(logout ->
                        logout.logoutUrl("/logout").logoutSuccessUrl("/home"));*/

        return http.build();
    }
}
