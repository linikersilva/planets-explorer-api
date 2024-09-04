package org.example.planetsexplorer.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record RecoveryJwtTokenDto(String token,
                                  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date expiresAt) {}
