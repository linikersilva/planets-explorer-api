package org.example.planetsexplorer.domain.repository;

import org.example.planetsexplorer.domain.model.SpaceProbe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceProbeRepository extends JpaRepository<SpaceProbe, Integer> {
    Optional<SpaceProbe> findByCurrentPlanet_IdAndXAndYAndIdNot(Integer planetId,
                                                                Integer x,
                                                                Integer y,
                                                                Integer currentSpaceProbeId);

    Optional<SpaceProbe> findByCurrentPlanet_IdAndXAndY(Integer planetId,
                                                        Integer x,
                                                        Integer y);
}
