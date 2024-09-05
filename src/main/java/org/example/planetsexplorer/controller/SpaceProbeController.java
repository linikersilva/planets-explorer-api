package org.example.planetsexplorer.controller;

import org.example.planetsexplorer.domain.service.SpaceProbeService;
import org.example.planetsexplorer.shared.dto.SpaceProbeActionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/space-probes")
public class SpaceProbeController {

    private final SpaceProbeService spaceProbeService;

    @Autowired
    public SpaceProbeController(SpaceProbeService spaceProbeService) {
        this.spaceProbeService = spaceProbeService;
    }

    @PostMapping("/move")
    public ResponseEntity<SpaceProbeActionDto> moveSpaceProbe(@AuthenticationPrincipal String userDetails,
                                                              @RequestBody SpaceProbeActionDto spaceProbeActionDto) {
        SpaceProbeActionDto response = spaceProbeService.moveSpaceProbe(spaceProbeActionDto, userDetails);
        return ResponseEntity.ok().body(response);
    }

}
