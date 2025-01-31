package org.example.rabbit.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/api/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            return http
                    .csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(
                           req->req.requestMatchers(AUTH_WHITELIST)
                                   .permitAll()).build();
//                                   .requestMatchers("/admin_only/**").hasAuthority("ADMIN")
//                                   .anyRequest()
    /// /                                .authenticated()
    /// /                ).userDetailsService(userDetailsServiceImp)
    /// /                .sessionManagement(session->session
    /// /                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    /// /                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    /// /                .exceptionHandling(
    /// /                        e->e.accessDeniedHandler(
    /// /                                        (request, response, accessDeniedException)->response.setStatus(403)
    /// /                                )
    /// /                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
    /// /                .logout(l->l
    /// /                        .logoutUrl("/logout")
    /// /                        .addLogoutHandler(logoutHandler)
    /// /                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()
    /// /                        ))
    /// /                .build();
//

    }
}
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers(SWAGGER_WHITELIST);
//    }
//}

