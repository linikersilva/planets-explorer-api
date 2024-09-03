package org.example.planetsexplorer.domain.model;

import jakarta.persistence.*;

@Entity(name = "usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    @Column(name = "senha")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_de_acesso_fk", nullable = false)
    private Role role;

    public User() {
    }

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
