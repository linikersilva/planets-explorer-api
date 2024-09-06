package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.Planet;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.PlanetRepository;
import org.example.planetsexplorer.domain.repository.projections.PlanetOccupiedPositionsProjection;
import org.example.planetsexplorer.shared.dto.CreatePlanetDto;
import org.example.planetsexplorer.shared.dto.PlanetResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {

    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Mock
    private UserService userService;

    @Test
    void createPlanetShouldReturnCreatedPlanetWhenValidParameters() {
        CreatePlanetDto createPlanetDto = new CreatePlanetDto("MARTE", 5, 5);
        User user = new User();
        user.setId(1);

        Planet savedPlanet = new Planet("MARTE", 5, 5, 25, user, LocalDateTime.now(), LocalDateTime.now(), user);
        savedPlanet.setId(1);

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(planetRepository.save(any(Planet.class))).thenReturn(savedPlanet);

        PlanetResponseDto response = planetService.createPlanet(createPlanetDto, "user@example.com");

        assertNotNull(response);
        assertEquals(savedPlanet.getName(), response.name());
        assertEquals(savedPlanet.getMaximumOccupancy(), response.maximumOccupancy());
        assertEquals(savedPlanet.getCreator().getId(), response.creatorId());

        verify(planetRepository).save(any(Planet.class));
    }

    @Test
    void createPlanetShouldThrowEntityNotFoundExceptionWhenUserEmailDoesNotExist() {
        CreatePlanetDto createPlanetDto = new CreatePlanetDto("MARTE", 5, 5);

        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> planetService.createPlanet(createPlanetDto, "naoexiste@example.com"));
    }

    @Test
    void updatePlanetShouldReturnUpdatedPlanetWhenValidParameters() {
        CreatePlanetDto createPlanetDto = new CreatePlanetDto("MARTE ATUALIZADO", 6, 6);
        User user = new User();
        user.setId(1);

        Planet existingPlanet = new Planet("MARTE", 5, 5, 25, user, LocalDateTime.now(), LocalDateTime.now(), user);
        existingPlanet.setId(1);

        when(planetRepository.findById(anyInt())).thenReturn(Optional.of(existingPlanet));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(planetRepository.save(any(Planet.class))).thenReturn(existingPlanet);

        PlanetResponseDto response = planetService.updatePlanet(1, createPlanetDto, "user@example.com");

        assertNotNull(response);
        assertEquals(createPlanetDto.name(), response.name());
        assertEquals(36, response.maximumOccupancy());
        assertEquals(user.getId(), response.creatorId());

        verify(planetRepository).save(any(Planet.class));
    }

    @Test
    void updatePlanetShouldThrowEntityNotFoundExceptionWhenPlanetDoesNotExist() {
        CreatePlanetDto createPlanetDto = new CreatePlanetDto("MARTE ATUALIZADO", 6, 6);

        when(planetRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> planetService.updatePlanet(1, createPlanetDto, "user@example.com"));
    }

    @Test
    void updatePlanetShouldThrowEntityNotFoundExceptionWhenUserEmailDoesNotExist() {
        CreatePlanetDto createPlanetDto = new CreatePlanetDto("MARTE ATUALIZADO", 6, 6);

        when(planetRepository.findById(anyInt())).thenReturn(Optional.of(new Planet()));
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> planetService.updatePlanet(1, createPlanetDto, "naoexiste@example.com"));
    }

    @Test
    void findPlanetByIdShouldReturnPlanetWhenIdExists() {
        User updater = new User();

        Planet planet = new Planet();
        planet.setId(1);
        planet.setName("PLANETA");
        planet.setWidth(5);
        planet.setHeight(3);
        planet.setMaximumOccupancy(15);
        planet.setCreatedAt(LocalDateTime.now());
        planet.setCreator(updater);
        planet.setUpdatedAt(LocalDateTime.now());
        planet.setUpdater(updater);

        when(planetRepository.findById(anyInt()))
                .thenReturn(Optional.of(planet));

        Planet planetFound = planetService.findPlanetById(1).orElse(null);

        assertEquals(planet.getId(), planetFound.getId());
        assertEquals(planet.getName(), planetFound.getName());
        assertEquals(planet.getWidth(), planetFound.getWidth());
        assertEquals(planet.getHeight(), planetFound.getHeight());
        assertEquals(planet.getMaximumOccupancy(), planetFound.getMaximumOccupancy());
        assertEquals(planet.getUpdater(), planetFound.getUpdater());
        assertNotNull(planetFound.getUpdatedAt());
        assertNotNull(planetFound.getCreatedAt());
    }

    @Test
    void findPlanetOccupiedPositionsShouldReturnListOfProjectionWhenValidPlanetId() {
        when(planetRepository.findPlanetOccupiedPositions(1)).thenReturn(List.of(mock(PlanetOccupiedPositionsProjection.class)));

        List<PlanetOccupiedPositionsProjection> result = planetService.findPlanetOccupiedPositions(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(planetRepository).findPlanetOccupiedPositions(1);
    }
}