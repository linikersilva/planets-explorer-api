package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.config.security.JwtTokenService;
import org.example.planetsexplorer.config.security.UserDetailsImpl;
import org.example.planetsexplorer.config.security.WebSecurityConfig;
import org.example.planetsexplorer.domain.model.Role;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.UserRepository;
import org.example.planetsexplorer.shared.dto.CreateUserDto;
import org.example.planetsexplorer.shared.dto.LoginUserDto;
import org.example.planetsexplorer.shared.dto.RecoveryJwtTokenDto;
import org.example.planetsexplorer.shared.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final WebSecurityConfig securityConfiguration;
    private final RoleService roleService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       UserRepository userRepository,
                       WebSecurityConfig securityConfiguration,
                       RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.securityConfiguration = securityConfiguration;
        this.roleService = roleService;
    }

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtTokenService.generateToken(userDetails);
        return new RecoveryJwtTokenDto(token,
                                       jwtTokenService.getExpiresAtFromToken(token));
    }

    @Transactional
    public UserResponseDto createUser(CreateUserDto createUserDto, String userDetails) {
        Role role = roleService.findRoleById(createUserDto.roleId());

        User creator = userRepository.findByEmail(userDetails)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o creatorId informado"));

        User newUser = new User(createUserDto.email(),
                                encryptUserPassword(createUserDto.password()),
                                role,
                                creator.getId(),
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                creator.getId());

        User user = userRepository.save(newUser);

        return new UserResponseDto(user.getId(),
                                   user.getEmail(),
                                   "******",
                                   role.getId(),
                                   creator.getId(),
                                   user.getCreatedAt(),
                                   user.getUpdatedAt(),
                                   creator.getId());
    }

    private String encryptUserPassword(String password) {
        return securityConfiguration.passwordEncoder().encode(password);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserResponseDto updateUser(Integer id, CreateUserDto createUserDto, String userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o id informado"));

        User updater = userRepository.findByEmail(userDetails)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o updaterId informado"));

        Optional.ofNullable(createUserDto.email()).ifPresent(user::setEmail);
        Optional.ofNullable(createUserDto.password()).ifPresent(password -> user.setPassword(encryptUserPassword(password)));
        Optional.ofNullable(createUserDto.roleId()).ifPresent(roleId -> {
            Role role = roleService.findRoleById(createUserDto.roleId());
            user.setRole(role);
        });

        user.setUpdaterId(updater.getId());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new UserResponseDto(user.getId(),
                                   user.getEmail(),
                                   "******",
                                   user.getRoleId(),
                                   user.getCreatorId(),
                                   user.getCreatedAt(),
                                   user.getUpdatedAt(),
                                   updater.getId());
    }
}
