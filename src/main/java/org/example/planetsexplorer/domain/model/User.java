package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;
import org.example.planetsexplorer.shared.enums.RoleName;

import java.time.LocalDateTime;

@Entity(name = "usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "senha", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_de_acesso_fk", nullable = false)
    private Role role;

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

    public User() {
    }

    public User(String email,
                String password,
                Role role,
                User creator,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                User updater) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updater = updater;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRoleName() {
        return role.getName().name();
    }

    public boolean isOwnerOf(SpaceProbe spaceProbe) {
        return this.id.equals(spaceProbe.getOwnerId());
    }

    public boolean hasRole(RoleName roleName) {
        return this.role.getName().name().equals(roleName.name());
    }
}
