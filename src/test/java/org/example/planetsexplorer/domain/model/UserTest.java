package org.example.planetsexplorer.domain.model;

import org.example.planetsexplorer.shared.enums.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user = new User();
    private final Role role = new Role();
    private SpaceProbe spaceProbe;

    @BeforeEach
    void setUp() {
        role.setId(1);
        role.setName(RoleName.ROLE_BASIC);
        user = new User("user@example.com", "password123",
                        role, 1, LocalDateTime.now(), LocalDateTime.now(), 1);
        spaceProbe = new SpaceProbe();
        spaceProbe.setOwner(user);
    }

    @Test
    void isOwnerOfShouldReturnTrueWhenUserIsSpaceProbeOwner() {
        assertEquals(user.getId(), spaceProbe.getOwnerId());
    }

    @Test
    void isOwnerOfShouldReturnFalseWhenUserIsNotSpaceProbeOwner() {
        User newOwner = new User();
        newOwner.setId(999);
        spaceProbe.setOwner(newOwner);
        assertNotEquals(user.getId(), spaceProbe.getOwnerId());
    }

    @Test
    void hasRoleShouldReturnTrueWhenUserHasRole() {
        assertEquals(user.getRoleName(), RoleName.ROLE_BASIC.name());
    }

    @Test
    void hasRoleShouldReturnFalseWhenUserDoesNotHaveRole() {
        assertNotEquals(user.getRoleName(), RoleName.ROLE_ADMIN.name());
    }
}