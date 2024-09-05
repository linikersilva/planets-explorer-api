package org.example.planetsexplorer.domain.repository;

import org.example.planetsexplorer.domain.model.Planet;
import org.example.planetsexplorer.domain.repository.projections.PlanetOccupiedPositionsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Integer> {

    @Query(value = "SELECT s.x, s.y "
                 + "FROM sonda s "
                 + "INNER JOIN planeta p ON s.planeta_fk = p.id "
                 + "WHERE p.id = :planetId", nativeQuery = true)
    List<PlanetOccupiedPositionsProjection> findPlanetOccupiedPositions(@Param("planetId") Integer planetId);
}
