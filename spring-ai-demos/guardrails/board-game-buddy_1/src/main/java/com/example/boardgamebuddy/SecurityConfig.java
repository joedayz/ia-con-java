package com.example.boardgamebuddy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) 
        throws Exception {
    http
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .anyRequest().authenticated())
    ;
    return http.build();
  }

  @Bean
  UserDetailsService userDetailsService() {  
    var user1 = User.builder()
        .username("mickey")
        .password("{noop}password")
        .roles("USER", "PREMIUM_USER")
        .build();

    var user2 = User.builder()
        .username("donald")
        .password("{noop}password")
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user1, user2);
  }

}
