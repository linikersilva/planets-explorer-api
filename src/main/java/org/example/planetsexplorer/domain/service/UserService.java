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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

    public void createUser(CreateUserDto createUserDto) {
        Role role = roleService.findRoleById(createUserDto.roleId());

        User creator = userRepository.findById(createUserDto.creatorId())
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o creatorId informado"));

        User newUser = new User(createUserDto.email(),
                                securityConfiguration.passwordEncoder().encode(createUserDto.password()),
                                role,
                                creator,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                creator);

        userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
