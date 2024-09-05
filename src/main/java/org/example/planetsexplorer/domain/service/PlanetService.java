package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.Planet;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.PlanetRepository;
import org.example.planetsexplorer.domain.repository.projections.PlanetOccupiedPositionsProjection;
import org.example.planetsexplorer.shared.dto.CreatePlanetDto;
import org.example.planetsexplorer.shared.dto.PlanetResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlanetService {

    private final PlanetRepository planetRepository;
    private final UserService userService;

    @Autowired
    public PlanetService(PlanetRepository planetRepository, UserService userService) {
        this.planetRepository = planetRepository;
        this.userService = userService;
    }

    @Transactional
    public PlanetResponseDto createPlanet(CreatePlanetDto createPlanetDto, String userDetails) {
        User creator = userService.findByEmail(userDetails)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o creatorId informado"));

        Planet newPlanet = new Planet(createPlanetDto.name(),
                                      createPlanetDto.width(),
                                      createPlanetDto.height(),
                                      createPlanetDto.width() * createPlanetDto.height(),
                                      creator,
                                      LocalDateTime.now(),
                                      LocalDateTime.now(),
                                      creator);

        Planet planet = planetRepository.save(newPlanet);

        return new PlanetResponseDto(planet.getId(),
                                     planet.getName(),
                                     planet.getWidth(),
                                     planet.getHeight(),
                                     planet.getMaximumOccupancy(),
                                     creator.getId(),
                                     planet.getCreatedAt(),
                                     planet.getUpdatedAt(),
                                     creator.getId());
    }

    @Transactional
    public PlanetResponseDto updatePlanet(Integer id, CreatePlanetDto createPlanetDto, String userDetails) {
        Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum planeta com o id informado"));

        User updater = userService.findByEmail(userDetails)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o updaterId informado"));

        Optional.ofNullable(createPlanetDto.name()).ifPresent(planet::setName);
        Optional.ofNullable(createPlanetDto.width()).ifPresent(planet::setWidth);
        Optional.ofNullable(createPlanetDto.height()).ifPresent(planet::setHeight);

        planet.setMaximumOccupancy(planet.getHeight() * planet.getWidth());
        planet.setUpdater(updater);
        planet.setUpdatedAt(LocalDateTime.now());

        planetRepository.save(planet);

        return new PlanetResponseDto(planet.getId(),
                                     planet.getName(),
                                     planet.getWidth(),
                                     planet.getHeight(),
                                     planet.getMaximumOccupancy(),
                                     planet.getCreator().getId(),
                                     planet.getCreatedAt(),
                                     planet.getUpdatedAt(),
                                     updater.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Planet> findPlanetById(Integer id) {
        return planetRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PlanetOccupiedPositionsProjection> findPlanetOccupiedPositions(Integer id) {
        return planetRepository.findPlanetOccupiedPositions(id);
    }
}
