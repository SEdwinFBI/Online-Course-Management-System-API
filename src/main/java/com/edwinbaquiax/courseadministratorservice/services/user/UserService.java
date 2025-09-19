package com.edwinbaquiax.courseadministratorservice.services.user;

import com.edwinbaquiax.courseadministratorservice.exceptions.RoleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UsernameIsExistsException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Role;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.mappers.UserProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IRoleRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.edwinbaquiax.courseadministratorservice.models.mappers.UserProfile.userEntityToUserResponseDTO;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(
                UserProfile::userEntityToUserResponseDTO
        ).toList();
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        return userEntityToUserResponseDTO(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findByUsername(String username) {
        return userEntityToUserResponseDTO(userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));
    }

    @Override
    @Transactional
    public UserResponseDTO save(User user) {
        //por defecto todos son estudiantes
        if(userRepository.existsByUsernameOrEmail(user.getUsername(),user.getEmail())){
            throw new UsernameIsExistsException();
        }

        Optional<Role> optionalRole =roleRepository.findByName("ROLE_STUDENT");

        Set<Role> roles = new HashSet<>();

        optionalRole.ifPresent(roles::add);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        return userEntityToUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateRoleUser(Long userId, Set<String> roleNames) {
        if(roleNames.isEmpty()) throw new RoleNotFoundException("Error, ningun rol especificado");


        User existUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new) ;

        Set<Role> roles = new HashSet<>();

        roleNames.forEach(
                name-> {
                    String roleName = name.toUpperCase().startsWith("ROLE_") ? name : "ROLE_"+name;
                    roleRepository.findByName(roleName.toUpperCase()).ifPresentOrElse(
                            (roles::add), ()-> {
                                throw new RoleNotFoundException(String.format("Error, El Rol %s no existe",name));
                            }
                    );
                }
        );
        existUser.setRoles(roles);
        return userEntityToUserResponseDTO(userRepository.save(existUser));
    }

    @Override
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(
                user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                }
        );
    }
    @Transactional
    @Override
    public UserResponseDTO update(Long userId, User userDetails) {
        User existUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(!existUser.getUsername().equals(userDetails.getUsername()) &&
                userRepository.existsByUsername(userDetails.getUsername())) {
            throw new UsernameIsExistsException();
        }

        existUser.setUsername(userDetails.getUsername());
        existUser.setEmail(userDetails.getEmail());
        if(userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()){
            existUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userEntityToUserResponseDTO(userRepository.save(existUser));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        User existUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(existUser);
    }
    @Override
    @Transactional
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
