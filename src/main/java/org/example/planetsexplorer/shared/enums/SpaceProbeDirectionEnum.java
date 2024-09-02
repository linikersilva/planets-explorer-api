package org.example.planetsexplorer.shared.enums;

import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;

public enum SpaceProbeDirectionEnum {
    NORTH(1, "NORTE"),
    EAST(2, "LESTE"),
    SOUTH(3, "SUL"),
    WEST(4, "OESTE");

    private final Integer id;
    private final String displayName;

    SpaceProbeDirectionEnum(Integer id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static boolean checkIfDirectionExists(Integer directionId) {
        boolean exists = Arrays.stream(SpaceProbeDirectionEnum.values())
                .anyMatch(enumDirection -> enumDirection.getId().equals(directionId));

        if (Boolean.FALSE.equals(exists)) {
            throw new EntityNotFoundException("Não existe direção com o id: " + directionId);
        }

        return true;
    }

    public Integer getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
