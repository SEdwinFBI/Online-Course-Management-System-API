package com.edwinbaquiax.courseadministratorservice.security;

import com.edwinbaquiax.courseadministratorservice.security.filters.JwtAuthenticationFilter;
import com.edwinbaquiax.courseadministratorservice.security.filters.JwtValidationFilter;
import com.edwinbaquiax.courseadministratorservice.services.user.IUserService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.SecretKey;
import java.util.Arrays;

//Intercambio de recursos de origen crusado

@Configuration
@EnableMethodSecurity(prePostEnabled = true)//seguridad en los handlers en controlladores
public class SpringSecurityConfig {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private IUserService userService;

    public AuthenticationManager authenticationManager() throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecretKey secretKey() {
        return generateKey();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, SecretKey secretKey) throws Exception {
        return httpSecurity.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                                authorizationManagerRequestMatcherRegistry
                                        .requestMatchers(HttpMethod.POST, "/api/v1/register/**", "/login/**").permitAll()
                                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                                .requestMatchers("api/v1/users").hasRole("ADMIN")
                                        .anyRequest().authenticated()//resto autenticado
                )
                //FILTROS
                //extiende de la clase UsernamePasswordAuthenticationFilter
                .addFilter(new JwtAuthenticationFilter(authenticationManager(),secretKey,userService))
                //extiende de la clase BasicAuthenticationFilter
                .addFilter(new JwtValidationFilter(authenticationManager(),secretKey))

                .csrf(AbstractHttpConfigurer::disable)//para formularios
                //Cors
                .cors(
                        httpSecurityCorsConfigurer ->
                                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(sManagement ->  //sin estado en el server
                        sManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }

    //Cors
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //ruta de aplicacion
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    //filtro
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> corsbean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        corsbean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsbean;
    }

    private SecretKey generateKey(){
        byte[] passwordDecodedBase64 = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(passwordDecodedBase64);
    }
}
