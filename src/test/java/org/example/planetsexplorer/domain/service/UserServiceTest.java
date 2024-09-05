package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.config.security.JwtTokenService;
import org.example.planetsexplorer.config.security.UserDetailsImpl;
import org.example.planetsexplorer.config.security.WebSecurityConfig;
import org.example.planetsexplorer.domain.model.Role;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.UserRepository;
import org.example.planetsexplorer.impl.AuthenticationImpl;
import org.example.planetsexplorer.shared.dto.CreateUserDto;
import org.example.planetsexplorer.shared.dto.LoginUserDto;
import org.example.planetsexplorer.shared.dto.RecoveryJwtTokenDto;
import org.example.planetsexplorer.shared.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private RoleService roleService;

    @Mock
    private WebSecurityConfig securityConfiguration;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void authenticateUserShouldReturnTokenAndExpiresAtWhenValidCredentials() {
        LoginUserDto dto = new LoginUserDto("email@gmail.com", "password");
        String token = "token";
        Date date = new Date();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new AuthenticationImpl());

        when(jwtTokenService.generateToken(any(UserDetailsImpl.class)))
                .thenReturn(token);

        when(jwtTokenService.getExpiresAtFromToken(anyString()))
                .thenReturn(date);

        RecoveryJwtTokenDto response = userService.authenticateUser(dto);

        assertEquals(token, response.token());
        assertEquals(date, response.expiresAt());
    }

    @Test
    void authenticateUserShouldThrowAuthenticationExceptionWhenInvalidCredentials() {
        LoginUserDto dto = new LoginUserDto("emailerrado@gmail.com", "senhaerrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("a"));

        assertThrows(BadCredentialsException .class, () -> userService.authenticateUser(dto));
    }

    @Test
    void createUserShouldReturnCreatedUserWhenValidParameters() {
        CreateUserDto createUserDto = new CreateUserDto("newuser@example.com", "password123", 1);
        String creatorEmail = "creator@example.com";

        Role role = new Role();
        role.setId(1);

        User creator = new User();
        creator.setId(100);
        creator.setEmail(creatorEmail);

        User newUser = new User(createUserDto.email(),
                                "encryptedPassword123",
                                role,
                                creator.getId(),
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                creator.getId());
        newUser.setId(200);

        when(roleService.findRoleById(createUserDto.roleId())).thenReturn(role);
        when(userRepository.findByEmail(creatorEmail)).thenReturn(Optional.of(creator));
        when(securityConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(createUserDto.password())).thenReturn("encryptedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserResponseDto response = userService.createUser(createUserDto, creatorEmail);

        assertNotNull(response);
        assertEquals(newUser.getId(), response.id());
        assertEquals(createUserDto.email(), response.email());
        assertEquals("******", response.password());
        assertEquals(role.getId(), response.roleId());
        assertEquals(creator.getId(), response.creatorId());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(roleService).findRoleById(createUserDto.roleId());
        verify(userRepository).findByEmail(creatorEmail);
        verify(securityConfiguration).passwordEncoder();
        verify(passwordEncoder).encode(createUserDto.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserShouldThrowEntityNotFoundExceptionWhenRoleIdDoesNotExist() {
        CreateUserDto createUserDto = new CreateUserDto("newuser@example.com", "password123", 99);
        String creatorEmail = "creator@example.com";

        when(roleService.findRoleById(createUserDto.roleId()))
                .thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException .class, () -> userService.createUser(createUserDto, creatorEmail));
    }

    @Test
    void createUserShouldThrowEntityNotFoundExceptionWhenCreatorEmailDoesNotExist() {
        CreateUserDto createUserDto = new CreateUserDto("newuser@example.com", "password123", 99);
        String creatorEmail = "emailquenaoexiste@example.com";

        when(roleService.findRoleById(createUserDto.roleId())).thenReturn(new Role());

        when(userRepository.findByEmail(anyString()))
                .thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException .class, () -> userService.createUser(createUserDto, creatorEmail));
    }

    @Test
    void findByEmailShouldReturnOptionalOfUserWhenEmailExists() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(new User()));

        Optional<User> optionalOfUser = userService.findByEmail("email@gmail.com");
        assertTrue(optionalOfUser.isPresent());
    }

    @Test
    void findByEmailShouldReturnOptionalOfUserWhenEmailDoesNotExist() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        Optional<User> optionalOfUser = userService.findByEmail("invalidemail@gmail.com");
        assertTrue(optionalOfUser.isEmpty());
    }

    @Test
    void findByIdShouldReturnUserWhenIdExists() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(new User()));

        User userResult = userService.findById(1);

        assertNotNull(userResult);
    }

    @Test
    void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException .class, () -> userService.findById(1));
    }

    @Test
    void updateUserShouldReturnUpdatedUserWhenValidParameters() {
        CreateUserDto createUserDto = new CreateUserDto("updateduser@example.com", "newpassword123", 2);
        String updaterEmail = "updater@example.com";

        Role role = new Role();
        role.setId(2);

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setEmail("olduser@example.com");
        existingUser.setRole(new Role());
        existingUser.setCreatorId(3);

        User updater = new User();
        updater.setId(4);
        updater.setEmail(updaterEmail);

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updateduser@example.com");
        updatedUser.setPassword("encryptedNewPassword123");
        updatedUser.setRole(role);
        updatedUser.setCreatorId(3);
        updatedUser.setUpdaterId(4);
        updatedUser.setUpdatedAt(LocalDateTime.now());
        updatedUser.setCreatedAt(existingUser.getCreatedAt());

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updaterEmail)).thenReturn(Optional.of(updater));
        when(securityConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encryptedNewPassword123");
        when(roleService.findRoleById(2)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDto response = userService.updateUser(1, createUserDto, updaterEmail);

        assertNotNull(response);
        assertEquals(updatedUser.getId(), response.id());
        assertEquals(createUserDto.email(), response.email());
        assertEquals("******", response.password());
        assertEquals(role.getId(), response.roleId());
        assertEquals(existingUser.getCreatorId(), response.creatorId());
        assertEquals(updatedUser.getCreatedAt(), response.createdAt());
        assertNotNull(response.updatedAt());
        assertEquals(updater.getId(), response.updaterId());

        verify(userRepository).findById(1);
        verify(userRepository).findByEmail(updaterEmail);
        verify(roleService).findRoleById(createUserDto.roleId());
        verify(securityConfiguration).passwordEncoder();
        verify(passwordEncoder).encode(createUserDto.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserShouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        CreateUserDto createUserDto = new CreateUserDto("updateduser@example.com", "newpassword123", 2);
        String updaterEmail = "updater@example.com";

        when(userRepository.findById(1)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException .class, () -> userService.updateUser(1, createUserDto, updaterEmail));
    }

    @Test
    void updateUserShouldThrowEntityNotFoundExceptionWhenUpdaterEmailDoesNotExist() {
        CreateUserDto createUserDto = new CreateUserDto("updateduser@example.com", "newpassword123", 2);
        String updaterEmail = "naoexiste@example.com";

        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(updaterEmail)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException .class, () -> userService.updateUser(1, createUserDto, updaterEmail));
    }

    @Test
    void updateUserShouldThrowEntityNotFoundExceptionWhenRoleIdDoesNotExist() {
        CreateUserDto createUserDto = new CreateUserDto("updateduser@example.com", "newpassword123", 2);
        String updaterEmail = "naoexiste@example.com";

        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(updaterEmail)).thenReturn(Optional.of(new User()));
        when(securityConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encryptedNewPassword123");
        when(roleService.findRoleById(2)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException .class, () -> userService.updateUser(1, createUserDto, updaterEmail));
    }
}