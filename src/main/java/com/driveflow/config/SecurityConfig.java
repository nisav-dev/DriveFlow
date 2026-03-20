package com.driveflow.config;

import com.driveflow.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // מאפשר @PreAuthorize ב-REST controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ── Public ───────────────────────────────────────────────
                .requestMatchers("/", "/search", "/vehicles/**",
                                 "/login", "/register",
                                 "/about", "/terms", "/cancellation", "/faq",
                                 "/static/**", "/css/**", "/js/**", "/images/**",
                                 "/h2-console/**").permitAll()

                // ── Swagger UI + OpenAPI ──────────────────────────────────
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                 "/v3/api-docs/**", "/api-docs/**").permitAll()

                // ── REST API — רכבים וסניפים ציבוריים ─────────────────────
                .requestMatchers("/api/vehicles/**", "/api/branches/**").permitAll()

                // ── REST API — הזמנות דורשות הרשאה (נאכף ב-@PreAuthorize) ──
                .requestMatchers("/api/bookings/**").hasAnyRole("ADMIN", "AGENT")

                // ── Admin UI ──────────────────────────────────────────────
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "AGENT")

                // ── Customer area ─────────────────────────────────────────
                .requestMatchers("/my-account/**", "/booking/**").hasRole("CUSTOMER")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((req, res, auth) -> {
                    boolean isAdminOrAgent = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                       || a.getAuthority().equals("ROLE_AGENT"));
                    SavedRequestAwareAuthenticationSuccessHandler handler =
                            new SavedRequestAwareAuthenticationSuccessHandler();
                    handler.setDefaultTargetUrl(isAdminOrAgent ? "/admin/dashboard" : "/");
                    handler.setAlwaysUseDefaultTargetUrl(false);
                    handler.onAuthenticationSuccess(req, res, auth);
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // ── Remember Me ───────────────────────────────────────────────
            .rememberMe(rm -> rm
                .userDetailsService(userDetailsService)
                .key("driveflow-remember-me-secret-key-2026")
                .tokenValiditySeconds(14 * 24 * 60 * 60)   // 14 ימים
                .rememberMeParameter("remember-me")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
