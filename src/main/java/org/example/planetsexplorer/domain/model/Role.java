package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;
import org.example.planetsexplorer.shared.enums.RoleName;

import java.time.LocalDateTime;

@Entity(name = "tipo_de_acesso")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nome", nullable = false, length = 30)
    private RoleName name;

    @Column(name = "data_de_criacao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_de_atualizacao", nullable = false)
    private LocalDateTime updatedAt;

    public Role() {
    }

    public Role(RoleName name) {
        this.name = name;
    }

    public RoleName getName() {
        return name;
    }
}
