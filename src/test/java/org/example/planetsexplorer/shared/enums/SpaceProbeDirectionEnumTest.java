package org.example.planetsexplorer.shared.enums;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpaceProbeDirectionEnumTest {

    @Test
    void testGetNextDirectionWhenLeftRotation() {
        Integer nextDirection = SpaceProbeDirectionEnum.getNextDirection(1, "LEFT");
        assertEquals(4, nextDirection);
    }

    @Test
    void testGetNextDirectionWhenRightRotation() {
        Integer nextDirection = SpaceProbeDirectionEnum.getNextDirection(1, "RIGHT");
        assertEquals(2, nextDirection);
    }

    @Test
    void testGetNextDirectionWhenInvalidRotation() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> SpaceProbeDirectionEnum.getNextDirection(1, "UP"));
        assertEquals("Tipo de rotação inválido", exception.getMessage());
    }

    @Test
    void testGetNextDirectionWhenInvalidDirectionId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> SpaceProbeDirectionEnum.getNextDirection(10, "LEFT"));
        assertEquals("Não existe direção com o id: 10", exception.getMessage());
    }

    @Test
    void testGetNameByIdWhenValidId() {
        String name = SpaceProbeDirectionEnum.getNameById(1);
        assertEquals("Norte", name);
    }

    @Test
    void testGetNameByIdWhenInvalidId() {
        int id = 10;
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> SpaceProbeDirectionEnum.getNameById(id));
        assertEquals("Não existe direção com o id: " + id, exception.getMessage());
    }

    @Test
    void testFromIdWhenValidId() {
        SpaceProbeDirectionEnum direction = SpaceProbeDirectionEnum.fromId(1);
        assertEquals(SpaceProbeDirectionEnum.NORTH, direction);
    }

    @Test
    void testFromIdWhenInvalidId() {
        int id = 10;
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> SpaceProbeDirectionEnum.fromId(id));
        assertEquals("Não existe direção com o id: " + id, exception.getMessage());
    }
}