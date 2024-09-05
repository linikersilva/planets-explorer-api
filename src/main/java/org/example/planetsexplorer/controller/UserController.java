package org.example.planetsexplorer.controller;

import org.example.planetsexplorer.domain.service.UserService;
import org.example.planetsexplorer.shared.dto.CreateUserDto;
import org.example.planetsexplorer.shared.dto.LoginUserDto;
import org.example.planetsexplorer.shared.dto.RecoveryJwtTokenDto;
import org.example.planetsexplorer.shared.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@AuthenticationPrincipal String userDetails,
                                                      @RequestBody CreateUserDto createUserDto) {
        UserResponseDto user = userService.createUser(createUserDto, userDetails);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id,
                                                      @AuthenticationPrincipal String userDetails,
                                                      @RequestBody CreateUserDto createUserDto) {
        UserResponseDto updatedUser = userService.updateUser(id, createUserDto, userDetails);
        return ResponseEntity.ok().body(updatedUser);
    }

}
