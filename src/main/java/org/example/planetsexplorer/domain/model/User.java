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

    @Column(name = "usuario_criador", nullable = false)
    private Integer creatorId;

    @Column(name = "data_de_criacao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_de_atualizacao", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "ultimo_usuario_que_atualizou", nullable = false)
    private Integer updaterId;

    public User() {
    }

    public User(String email,
                String password,
                Role role,
                Integer creatorId,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                Integer updaterId) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updaterId = updaterId;
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

    public Integer getRoleId() {
        return role.getId();
    }

    public String getRoleName() {
        return role.getName().name();
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOwnerOf(SpaceProbe spaceProbe) {
        return this.id.equals(spaceProbe.getOwnerId());
    }

    public boolean hasRole(RoleName roleName) {
        return this.role.getName().name().equals(roleName.name());
    }
}
