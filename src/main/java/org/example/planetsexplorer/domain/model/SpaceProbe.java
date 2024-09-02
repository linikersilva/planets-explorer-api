package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;
import org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum;

@Entity(name = "sonda")
public class SpaceProbe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer x;

    @Column
    private Integer y;

    @Column(name = "direcao")
    private Integer direction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planeta_fk")
    private Planet actualPlanet;

    public SpaceProbe() {
    }

    public SpaceProbe(Integer x, Integer y, Integer direction, Planet actualPlanet) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.actualPlanet = actualPlanet;
    }

    public boolean areValidCoordinates() {
        return isValidPositionValue(this.x) && isValidPositionValue(this.y);
    }

    private boolean isValidPositionValue(Integer position) {
        return position >= 0 && position <= 4;
    }

    public boolean isValidDirection(Integer direction) {
        return SpaceProbeDirectionEnum.checkIfDirectionExists(direction);
    }

}
