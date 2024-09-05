package org.example.planetsexplorer.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PlanetResponseDto(Integer id,
                                String name,
                                Integer width,
                                Integer height,
                                Integer maximumOccupancy,
                                Integer creatorId,
                                @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")LocalDateTime createdAt,
                                @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")LocalDateTime updatedAt,
                                Integer updaterId){}
