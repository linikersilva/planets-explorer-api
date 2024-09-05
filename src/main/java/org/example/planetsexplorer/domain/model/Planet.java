package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "planeta")
public class Planet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "largura")
    private Integer width;

    @Column(name = "altura")
    private Integer height;

    @Column(name = "ocupacao_maxima")
    private Integer maximumOccupancy;

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

    public Planet() {
    }

    public Planet(String name, Integer width, Integer height,
                  Integer maximumOccupancy, User creator,
                  LocalDateTime createdAt, LocalDateTime updatedAt, User updater) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.maximumOccupancy = maximumOccupancy;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updater = updater;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getMaximumOccupancy() {
        return maximumOccupancy;
    }

    public User getCreator() {
        return creator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setMaximumOccupancy(Integer maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdater(User updater) {
        this.updater = updater;
    }
}
