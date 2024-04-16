package mx.edu.utez.carsishop.config;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.jwt.JwtAuthenticationFilter;
import mx.edu.utez.carsishop.models.user.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings({"removal"})
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http.cors().and()
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/captcha/**").permitAll()
                .requestMatchers("/api/auth/**","/api/captcha/**").permitAll()
                .requestMatchers("/api/address/**").hasAnyAuthority(Role.CUSTOMER.name(),Role.ADMIN.name())
                .requestMatchers("/api/card/**","/api/order/makeOrder","/api/clothesCart/**").hasAnyAuthority(Role.CUSTOMER.name(),Role.ADMIN.name())
                .requestMatchers("/api/category/find-all","/api/subcategories/find-all").permitAll()
                .requestMatchers("/api/category/**","/api/subcategories/**","/api/users/find-all","/api/users/register-admin").hasAnyAuthority(Role.ADMIN.name())
                .requestMatchers("/api/clothes/isAccepted","/api/sellers/find-all","/api/sellers/change-status").hasAnyAuthority(Role.ADMIN.name())
                .requestMatchers("/api/clothes/find-all","/api/clothes/getOne/**","/api/clothes/getByCategory/**","/api/clothes/getByCategoryAndSubcategory/**","/api/clothes/getAllClothesOrderedByPrice").permitAll()
                .requestMatchers("/api/images/**","/api/clothes/create","/api/clothes/update","/api/clothes/update/stock","/api/clothes/disable/**").hasAnyAuthority(Role.SELLER.name())
                .requestMatchers("/api/order/updateStatus","/api/sellers/").hasAnyAuthority(Role.SELLER.name(),Role.ADMIN.name())
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
