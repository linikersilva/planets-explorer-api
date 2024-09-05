package org.example.planetsexplorer.controller;

import jakarta.validation.Valid;
import org.example.planetsexplorer.domain.service.UserService;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationCreationGroup;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationUpdateGroup;
import org.example.planetsexplorer.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody @Valid LoginUserDto loginUserDto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@AuthenticationPrincipal String userDetails,
                                                      @RequestBody @Validated(BeanValidationCreationGroup.class)
                                                      CreateUserDto createUserDto) {
        UserResponseDto user = userService.createUser(createUserDto, userDetails);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id,
                                                      @AuthenticationPrincipal String userDetails,
                                                      @RequestBody @Validated(BeanValidationUpdateGroup.class)
                                                      CreateUserDto createUserDto) {
        UserResponseDto updatedUser = userService.updateUser(id, createUserDto, userDetails);
        return ResponseEntity.ok().body(updatedUser);
    }

}
