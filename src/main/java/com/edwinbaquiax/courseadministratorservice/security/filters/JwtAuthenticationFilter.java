package com.edwinbaquiax.courseadministratorservice.security.filters;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.services.user.IUserService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private SecretKey secretKey;
    private AuthenticationManager authenticationManager;
    private IUserService userService;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecretKey secretKey, IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.secretKey=secretKey;
        this.userService = userService;
    }

    //Autenticacion
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        User user = null;
        String username = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        }catch (DatabindException e) {
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                password);

        //authenticate llama a JpaUserDetailsService y auto compara password
        return authenticationManager.authenticate(authenticationToken);
    }

    //Generacion de token
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {


        org.springframework.security.core.userdetails.User user= (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        String username = user.getUsername();
        Long userId = userService.findByUsername(username).getId();

        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        Claims claims = Jwts
                .claims()
               // .add("authorities",roles)
                .add("authorities", new ObjectMapper().writeValueAsString(roles))
                .add("userId",userId)
                .build();

        String jwtToken = Jwts
                .builder()
                .subject(username)
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis()+(24*3600000)))
                .issuedAt(new Date())
                .signWith(secretKey, Jwts.SIG.HS256)//firmado
                .compact();
        
        userService.updateLastLogin(username);

        response.addHeader("Authorization",String.format("Bearer %s",jwtToken));
        Map<String,String> body = new HashMap<>();
        body.put("token",jwtToken);
        body.put("username",username);
        body.put("message",String.format("Hola %s has iniciado sesion con exito",username));
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String,String> body = new HashMap<>();
        body.put("message","Error en la autenticacion, error en username o password incorrectos");
        body.put("error",failed.getMessage());
        //json
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

    }

}
