package org.example.planetsexplorer.domain.repository;

import org.example.planetsexplorer.domain.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Integer> {
}
