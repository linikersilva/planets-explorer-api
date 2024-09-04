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

    public Planet(Integer width, Integer height) {
        this.width = width;
        this.height = height;
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

    public boolean isValidMaximumOccupancy(Integer newMaximumOccupancy) {
        return (this.height * this.width) <= newMaximumOccupancy;
    }
}
