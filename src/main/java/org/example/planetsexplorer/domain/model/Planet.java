package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;

@Entity(name = "planeta")
public class Planet {

    private static final int DEFAULT_LENGTH_VALUE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "largura")
    private Integer width;

    @Column(name = "altura")
    private Integer height;

    @Column(name = "ocupacao_maxima")
    private Integer maximumOccupancy;

    public Planet() {
    }

    public Planet(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    public boolean isValidPlanetExtension() {
        return isValidLength(this.width) && isValidLength(this.height);
    }

    private boolean isValidLength(Integer length) {
        return DEFAULT_LENGTH_VALUE == length;
    }

    private boolean isValidMaximumOccupancy() {
        return (this.height * this.width) <= this.maximumOccupancy;
    }
}
