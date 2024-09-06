package org.example.planetsexplorer.domain.model;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpaceProbeTest {

    private SpaceProbe spaceProbe;
    private Planet mockedPlanet;

    @BeforeEach
    void setUp() {
        mockedPlanet = mock(Planet.class);
        when(mockedPlanet.getWidth()).thenReturn(5);
        when(mockedPlanet.getHeight()).thenReturn(5);

        spaceProbe = new SpaceProbe(1, 1, SpaceProbeDirectionEnum.NORTH.getId(),
                mockedPlanet, null, null, null, null, null);
    }

    @Test
    void testMoveForwardNorth() {
        spaceProbe.moveForward();
        assertEquals(1, spaceProbe.getX());
        assertEquals(2, spaceProbe.getY());
    }

    @Test
    void testMoveForwardSouth() {
        spaceProbe.setDirection(SpaceProbeDirectionEnum.SOUTH.getId());
        spaceProbe.moveForward();
        assertEquals(1, spaceProbe.getX());
        assertEquals(0, spaceProbe.getY());
    }

    @Test
    void testMoveForwardEast() {
        spaceProbe.setDirection(SpaceProbeDirectionEnum.EAST.getId());
        spaceProbe.moveForward();
        assertEquals(2, spaceProbe.getX());
        assertEquals(1, spaceProbe.getY());
    }

    @Test
    void testMoveForwardWest() {
        spaceProbe.setDirection(SpaceProbeDirectionEnum.WEST.getId());
        spaceProbe.moveForward();
        assertEquals(0, spaceProbe.getX());
        assertEquals(1, spaceProbe.getY());
    }

    @Test
    void testMoveForwardInvalidDirection() {
        int direction = -1;
        spaceProbe.setDirection(direction);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, spaceProbe::moveForward);
        assertEquals("Não existe direção com o id: " + direction, exception.getMessage());
    }

    @Test
    void testLeftRotate() {
        spaceProbe.setDirection(SpaceProbeDirectionEnum.NORTH.getId());
        spaceProbe.leftRotate();
        assertEquals(SpaceProbeDirectionEnum.WEST.getId(), spaceProbe.getDirection());
    }

    @Test
    void testRightRotate() {
        spaceProbe.setDirection(SpaceProbeDirectionEnum.NORTH.getId());
        spaceProbe.rightRotate();
        assertEquals(SpaceProbeDirectionEnum.EAST.getId(), spaceProbe.getDirection());
    }

    @Test
    void testCoordinatesAreInsidePlanetBorders() {
        assertTrue(spaceProbe.coordinatesAreInsidePlanetBorders());
    }

    @Test
    void testCoordinatesAreOutsidePlanetBorders() {
        spaceProbe.setX(6);
        assertFalse(spaceProbe.coordinatesAreInsidePlanetBorders());
    }

    @Test
    void testUnlinkPlanet() {
        spaceProbe.setX(3);
        spaceProbe.setY(4);
        spaceProbe.setDirection(SpaceProbeDirectionEnum.NORTH.getId());
        spaceProbe.setCurrentPlanet(mockedPlanet);

        spaceProbe.unlinkPlanet();

        assertNull(spaceProbe.getX());
        assertNull(spaceProbe.getY());
        assertNull(spaceProbe.getDirection());
        assertNull(spaceProbe.getCurrentPlanet());
    }
}