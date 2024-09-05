package org.example.planetsexplorer.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserResponseDto(Integer id,
                              String email,
                              String password,
                              Integer roleId,
                              Integer creatorId,
                              @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")LocalDateTime createdAt,
                              @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")LocalDateTime updatedAt,
                              Integer updaterId){}
