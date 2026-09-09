package com.starterkit.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.api-key.read:}")
    private String readKey;

    @Value("${security.api-key.admin:}")
    private String adminKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new ApiKeyAuthFilter(readKey, adminKey),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(eh -> eh.authenticationEntryPoint(
                        (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                ))
                .authorizeRequests(requests -> requests

                        // MAIN PAGE + ESTÁTICOS
                        .antMatchers("/", "/index", "/index.html", "/error", "/fornecedores/**", "/equipamentos/**", "/artigos/**").permitAll()
                        .antMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/uploads/**").permitAll()

                        // ADD SWAGGER
                        .antMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml")
                        .permitAll()
                        .antMatchers("/actuator/health").permitAll()

                        // GET público
                        .antMatchers(HttpMethod.GET, "/api/todos/**", "/api/users/**", "/api/equipamentos/**","/api/fornecedores/**")
                        .permitAll()

                        // Só ADMIN pode escrever
                        .antMatchers(HttpMethod.POST, "/api/todos/**", "/api/users/**", "/api/equipamentos/**","/api/fornecedores/**")
                        .hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/api/todos/**", "/api/users/**", "/api/equipamentos/**","/api/fornecedores/**")
                        .hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/todos/**", "/api/users/**", "/api/equipamentos/**","/api/fornecedores/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                );
    }
}
