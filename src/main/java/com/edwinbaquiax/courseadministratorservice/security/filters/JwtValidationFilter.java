package com.edwinbaquiax.courseadministratorservice.security.filters;

import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserPrincipal;
import com.edwinbaquiax.courseadministratorservice.security.SimpleGrantedAuthorityJsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class JwtValidationFilter extends BasicAuthenticationFilter {

    private SecretKey secretKey;

    public JwtValidationFilter(AuthenticationManager authenticationManager, SecretKey secretKey) {
        super(authenticationManager);
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            //recurso publico
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace("Bearer ", "");
        try {


            Claims claims = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token).getPayload();

            String username = claims.getSubject();
            //segun clave
//            String username2 = (String) claims.get("sub");
            Object authoritiesClaims = claims.get("authorities");
            Object userIdObj = claims.get("userId");
            Long userIdClaim = userIdObj == null ? null : Long.valueOf(userIdObj.toString());

            //Obtencion de roles y convirtiendolos a una collecion
            Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
                    //añadiendo sobreescritura rol -> authority
                    .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                    .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new UserPrincipal(userIdClaim, username),
                            null,
                            authorities);

            //Autenticacion
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            chain.doFilter(request, response);
        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "Toekn jwt invalido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
        }
    }

}
