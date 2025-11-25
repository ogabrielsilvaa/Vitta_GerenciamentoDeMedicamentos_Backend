package com.vitta.vittaBackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserAuthenticationFilter userAuthenticationFilter;

    // Adicione esta constante com os endpoints do Swagger
    private static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
    };

    private static final String[] AUTENTICACAO_ENDPOINTS = {
            "/api/usuarios/cadastrar",
            "/api/usuarios/login"
    };

    public static final String[] ENDPOINTS_SEM_AUTENTICACAO = {
            "/",
            "/install",
            "/download-apk",
            "/index.html",
            "/install.html",
            "/app-release.apk",
    };

    public static final String[] ENDPOINTS_WEB_PAGES = {
            "/",
            "/install",
            "/download-apk",
            "/download/app"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Libera os endpoints do Swagger
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()

                        // Libera criação de usuário e login sem token
                        .requestMatchers(AUTENTICACAO_ENDPOINTS).permitAll()

                        .requestMatchers(ENDPOINTS_SEM_AUTENTICACAO).permitAll()

                        .requestMatchers(ENDPOINTS_WEB_PAGES).permitAll()


                        // Endpoints de teste com suas respectivas permissões
                        .requestMatchers("/api/usuarios/test").authenticated()
                        .requestMatchers("/api/usuarios/test/customer").hasRole("CUSTOMER")

                        // Qualquer outra requisição não listada acima exigirá autenticação
                        .anyRequest().authenticated()
                )

                // Adiciona o filtro JWT antes do filtro padrão do Spring
                .addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite qualquer origem (celular, emulador, web) em desenvolvimento
        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
