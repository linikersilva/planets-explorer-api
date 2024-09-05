package org.example.planetsexplorer.controller;

import jakarta.validation.Valid;
import org.example.planetsexplorer.domain.service.SpaceProbeService;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationCreationGroup;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationLandProbeGroup;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationUpdateGroup;
import org.example.planetsexplorer.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/space-probes")
public class SpaceProbeController {

    private final SpaceProbeService spaceProbeService;

    @Autowired
    public SpaceProbeController(SpaceProbeService spaceProbeService) {
        this.spaceProbeService = spaceProbeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceProbeResponseDto> findById(@PathVariable Integer id) {
        SpaceProbeResponseDto response = spaceProbeService.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<Page<SpaceProbeResponseDto>> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        Page<SpaceProbeResponseDto> response = spaceProbeService.findAll(page, size);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<SpaceProbeResponseDto> createSpaceProbe(@AuthenticationPrincipal String userDetails,
                                                                  @RequestBody @Validated({BeanValidationCreationGroup.class})
                                                                  CreateSpaceProbeDto createSpaceProbeDto) {
        SpaceProbeResponseDto spaceProbe = spaceProbeService.createSpaceProbe(createSpaceProbeDto, userDetails);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(spaceProbe.id()).toUri();
        return ResponseEntity.created(uri).body(spaceProbe);
    }

    @PatchMapping("/move")
    public ResponseEntity<SpaceProbeActionDto> moveSpaceProbe(@AuthenticationPrincipal String userDetails,
                                                              @RequestBody @Valid SpaceProbeActionDto spaceProbeActionDto) {
        SpaceProbeActionDto response = spaceProbeService.moveSpaceProbe(spaceProbeActionDto, userDetails);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/update-owner/{id}")
    public ResponseEntity<SpaceProbeResponseDto> updateSpaceProbeOwner(@PathVariable Integer id,
                                                                       @AuthenticationPrincipal String userDetails,
                                                                       @RequestBody @Validated({BeanValidationUpdateGroup.class})
                                                                       CreateSpaceProbeDto createSpaceProbeDto) {
        SpaceProbeResponseDto updatedSpaceProbe = spaceProbeService.updateSpaceProbeOwner(id, createSpaceProbeDto, userDetails);
        return ResponseEntity.ok().body(updatedSpaceProbe);
    }

    @PatchMapping("/land/{id}")
    public ResponseEntity<SpaceProbeResponseDto> landSpaceProbe(@PathVariable Integer id,
                                                                @AuthenticationPrincipal String userDetails,
                                                                @RequestBody @Validated(BeanValidationLandProbeGroup.class)
                                                                CreateSpaceProbeDto createSpaceProbeDto) {
        SpaceProbeResponseDto landedSpaceProbe = spaceProbeService.landSpaceProbe(id, createSpaceProbeDto, userDetails);
        return ResponseEntity.ok().body(landedSpaceProbe);
    }

}
