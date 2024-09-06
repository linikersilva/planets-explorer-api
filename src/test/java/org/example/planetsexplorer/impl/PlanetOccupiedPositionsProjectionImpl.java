package org.example.planetsexplorer.impl;

import org.example.planetsexplorer.domain.repository.projections.PlanetOccupiedPositionsProjection;

public class PlanetOccupiedPositionsProjectionImpl implements PlanetOccupiedPositionsProjection {
    private Integer x;
    private Integer y;

    @Override
    public Integer getX() {
        return this.x;
    }

    @Override
    public Integer getY() {
        return this.y;
    }

    public PlanetOccupiedPositionsProjectionImpl(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
}
