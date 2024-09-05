package org.example.planetsexplorer.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SpaceProbeResponseDto(Integer id,
                                    Integer x,
                                    Integer y,
                                    String direction,
                                    String currentPlanet,
                                    Integer ownerId,
                                    Integer creatorId,
                                    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")LocalDateTime createdAt,
                                    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")LocalDateTime updatedAt,
                                    Integer updaterId){}
