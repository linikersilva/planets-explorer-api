package org.example.planetsexplorer.shared.dto;

public record CreateUserDto(String email,
                            String password,
                            Integer roleId,
                            Integer creatorId){}
