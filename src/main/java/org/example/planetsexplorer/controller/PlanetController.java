package org.example.planetsexplorer.controller;

import org.example.planetsexplorer.domain.service.PlanetService;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationCreationGroup;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationUpdateGroup;
import org.example.planetsexplorer.shared.dto.CreatePlanetDto;
import org.example.planetsexplorer.shared.dto.PlanetResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/planets")
public class PlanetController {

    private final PlanetService planetService;

    @Autowired
    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @PostMapping
    public ResponseEntity<PlanetResponseDto> createPlanet(@AuthenticationPrincipal String userDetails,
                                                          @RequestBody @Validated(BeanValidationCreationGroup.class)
                                                          CreatePlanetDto createPlanetDto) {
        PlanetResponseDto planet = planetService.createPlanet(createPlanetDto, userDetails);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(planet.id()).toUri();
        return ResponseEntity.created(uri).body(planet);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<PlanetResponseDto> updatePlanet(@PathVariable Integer id,
                                                          @AuthenticationPrincipal String userDetails,
                                                          @RequestBody @Validated(BeanValidationUpdateGroup.class)
                                                          CreatePlanetDto createPlanetDto) {
        PlanetResponseDto updatedPlanet = planetService.updatePlanet(id, createPlanetDto, userDetails);
        return ResponseEntity.ok().body(updatedPlanet);
    }

}
