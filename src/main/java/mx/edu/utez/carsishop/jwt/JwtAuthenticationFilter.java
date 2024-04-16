package mx.edu.utez.carsishop.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
       
        final String token = getTokenFromRequest(request);
        final String username;

        if (token==null)
        {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            username=jwtService.getUsernameFromToken(token);

            if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
            {
                UserDetails userDetails=userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
        } catch (ExpiredJwtException ex) {
            CustomResponse<String> customResponse = new CustomResponse<>();
            customResponse.setError(true);
            customResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            customResponse.setMessage("Token expirado");

            // configurar la respuesta del servlet con el contenido de la instancia de CustomResponse
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), customResponse);

            return;
        } catch (SignatureException e){
            CustomResponse<String> customResponse = new CustomResponse<>();
            customResponse.setError(true);
            customResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            customResponse.setMessage("Token inválido");

            // configurar la respuesta del servlet con el contenido de la instancia de CustomResponse
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), customResponse);

            return;
        }catch (Exception e){
            CustomResponse<String> customResponse = new CustomResponse<>();
            customResponse.setError(true);
            customResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            customResponse.setMessage("Error desconocido de autenticación");

            // configurar la respuesta del servlet con el contenido de la instancia de CustomResponse
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), customResponse);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer "))
        {
            return authHeader.substring(7);
        }
        return null;
    }



    
}
