package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.Role;
import org.example.planetsexplorer.domain.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Role findRoleById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("O id especificado não corresponde a um tipo de acesso"));
    }
}
