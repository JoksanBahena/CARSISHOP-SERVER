package mx.edu.utez.carsishop.Config;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.Jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http.cors().and()
            .csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers("/api/auth/**","/api/captcha/**").permitAll()
                      .requestMatchers("/api/address/**").hasAnyAuthority("CUSTOMER","SELLER")
                        .requestMatchers("/api/card/**","/api/order/makeOrder","/api/clothesCart/**").hasAnyAuthority("CUSTUMER")
                      .requestMatchers("/api/category/find-all","/api/subcategories/find-all").permitAll()
                      .requestMatchers("/api/category/**","/api/subcategories/**","/api/users/find-all","/api/users/register-admin").hasAnyAuthority("ADMIN")
                      .requestMatchers("/api/clothes/isAccepted","/api/sellers/find-all","/api/sellers/change-status").hasAnyAuthority("ADMIN")
                      .requestMatchers("/api/clothes/find-all","/api/clothes/getOne/**","/api/clothes/getByCategory/**","/api/clothes/getByCategoryAndSubcategory/**","/api/clothes/getAllClothesOrderedByPrice").permitAll()
                          .requestMatchers("/api/images/**","/api/clothes/create","/api/clothes/update","/api/clothes/update/stock","/api/clothes/disable/**").hasAnyAuthority("SELLER")
                      .requestMatchers("/api/order/updateStatus","/api/sellers/").hasAnyAuthority("SELLER","ADMIN")
                .anyRequest().authenticated()
                )
            .sessionManagement(sessionManager->
                sessionManager
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
