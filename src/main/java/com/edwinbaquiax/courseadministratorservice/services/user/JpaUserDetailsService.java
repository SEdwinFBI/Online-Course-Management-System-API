package com.edwinbaquiax.courseadministratorservice.services.user;

import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)  //Login
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,UserNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException(String.format("Usuario %s no existe",username));
        }
        User user = userOptional.orElseThrow();

        if(!user.isEnabled())  throw new UserNotFoundException(String.format("Usuario %s no Valido",username));

        //Para roles
        List<GrantedAuthority>  authorities = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))//para setear
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),//auto configurable y auto comparable
                user.isEnabled(),
                true,
                true ,
                true,
                authorities
        );
    }
}
