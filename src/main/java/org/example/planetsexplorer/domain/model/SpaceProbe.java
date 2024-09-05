package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;
import org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum;
import org.example.planetsexplorer.shared.exception.BusinessException;

import java.time.LocalDateTime;

import static org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum.*;

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
    private Planet currentPlanet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_dono", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_criador", nullable = false)
    private User creator;

    @Column(name = "data_de_criacao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_de_atualizacao", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ultimo_usuario_que_atualizou", nullable = false)
    private User updater;

    public SpaceProbe() {
    }

    public SpaceProbe(Integer x, Integer y, Integer direction, Planet currentPlanet,
                      User owner, User creator, LocalDateTime createdAt,
                      LocalDateTime updatedAt, User updater) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.currentPlanet = currentPlanet;
        this.owner = owner;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updater = updater;
    }

    public Integer getId() {
        return id;
    }

    public Integer getDirection() {
        return direction;
    }

    public Planet getCurrentPlanet() {
        return currentPlanet;
    }

    public Integer getCurrentPlanetId() {
        return currentPlanet.getId();
    }

    public String getCurrentPlanetName() {
        return currentPlanet.getName();
    }

    public Integer getOwnerId() {
        return owner.getId();
    }

    public Integer getCreatorId() {
        return creator.getId();
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getUpdaterId() {
        return updater.getId();
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public void setCurrentPlanet(Planet currentPlanet) {
        this.currentPlanet = currentPlanet;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdater(User updater) {
        this.updater = updater;
    }

    public void moveForward() {
        SpaceProbeDirectionEnum currentDirection = fromId(getDirection());

        switch (currentDirection) {
            case NORTH:
                y++;
                break;
            case EAST:
                x++;
                break;
            case SOUTH:
                y--;
                break;
            case WEST:
                x--;
                break;
            default:
                throw new BusinessException("O eixo de movimento deve ser X ou Y");
        }
    }

    public void leftRotate() {
        direction = SpaceProbeDirectionEnum.getNextDirection(getDirection(), "LEFT");
    }

    public void rightRotate() {
        direction = SpaceProbeDirectionEnum.getNextDirection(getDirection(), "RIGHT");
    }

    public boolean coordinatesAreInsidePlanetBorders() {
        return x >= 1 && y >= 1 && x <= currentPlanet.getWidth() && y <= currentPlanet.getHeight();
    }

    public void unlinkPlanet() {
        x = null;
        y = null;
        direction = null;
        currentPlanet = null;
    }
}
