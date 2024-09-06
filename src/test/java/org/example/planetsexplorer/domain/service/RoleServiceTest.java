package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.Role;
import org.example.planetsexplorer.domain.repository.RoleRepository;
import org.example.planetsexplorer.shared.enums.RoleName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void findRoleByIdShouldReturnRoleWhenIdExists() {
        Role role = new Role();
        role.setId(1);
        role.setName(RoleName.ROLE_BASIC);

        when(roleRepository.findById(anyInt()))
                .thenReturn(Optional.of(role));

        Role roleFound = roleService.findRoleById(1);

        assertEquals(role.getId(), roleFound.getId());
        assertEquals(role.getName(), roleFound.getName());
    }

    @Test
    void findRoleByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        when(roleRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.findRoleById(99));

    }
}