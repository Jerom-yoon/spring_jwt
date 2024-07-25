package com.example.jwt.springjwt.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.jwt.springjwt.jwt.JWTFilter;
import com.example.jwt.springjwt.jwt.JWTUtil;
import com.example.jwt.springjwt.jwt.LoginFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,JWTUtil jwtUtil) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil =jwtUtil;
    }
  //AuthenticationManager Bean 등록
	@Bean
	  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
	
	      return configuration.getAuthenticationManager();
	  }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
				.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
					
					@Override
	                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration configuration = new CorsConfiguration();

	                    configuration.setAllowedOrigins(Collections.singletonList("http://192.168.219.109:8082"));
	                    configuration.setAllowedMethods(Collections.singletonList("*"));
	                    configuration.setAllowCredentials(true);
	                    configuration.setAllowedHeaders(Collections.singletonList("*"));
	                    configuration.setMaxAge(3600L);

						configuration.setExposedHeaders(Collections.singletonList("Authorization"));

	                    return configuration;
					}
				}));
		http
				.csrf((auth) -> auth.disable());
		
		http
				.formLogin((auth)->auth.disable());
		
		http
				.httpBasic((auth) -> auth.disable());
		
		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/login", "/", "/join").permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());
		http
				.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
		http
				.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return http.build();
	}
}
