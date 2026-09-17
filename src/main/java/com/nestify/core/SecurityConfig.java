package com.nestify.core;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Configures the HTTP security filter chain.
	 *
	 * @param http HttpSecurity configuration builder
	 * @return Configured SecurityFilterChain instance
	 * @throws Exception if an error occurs during security chain configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- CORS burada aktif edilmeli
				.csrf(csrf -> csrf.disable()) // Stateless REST API için kapatılır
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll());

		return http.build();
	}

	/**
	 * Configures CORS settings for the application.
	 *
	 * @return CorsConfigurationSource instance with registered CORS rules
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		// Domaininizin sonuna slash (/) koymadan ekleyin:
		config.setAllowedOrigins(List.of(
				"https://nestify.hamdiyecicek.tech",
				"http://localhost",
				"http://localhost:5173",
				"http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		config.setAllowedHeaders(List.of(
				"Authorization",
				"Content-Type",
				"Accept",
				"Origin",
				"X-Requested-With",
				"Access-Control-Request-Method",
				"Access-Control-Request-Headers"));
		config.setExposedHeaders(List.of("Authorization", "Content-Type"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	/**
	 * Configures and provides the BCrypt password encoder bean.
	 *
	 * @return PasswordEncoder instance using BCrypt hashing algorithm
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}