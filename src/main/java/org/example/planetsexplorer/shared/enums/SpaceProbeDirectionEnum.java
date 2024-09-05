package org.example.planetsexplorer.shared.enums;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.shared.exception.BusinessException;

import java.util.Arrays;
import java.util.Objects;

public enum SpaceProbeDirectionEnum {
    NORTH(1, "Norte", 4, 2),
    EAST(2, "Leste", 1, 3),
    SOUTH(3, "Sul", 2, 4),
    WEST(4, "Oeste", 3, 1);

    private final Integer id;
    private final String name;
    private final Integer leftRotation;
    private final Integer rightRotation;

    SpaceProbeDirectionEnum(Integer id, String name,
                            Integer leftRotation, Integer rightRotation) {
        this.id = id;
        this.name = name;
        this.leftRotation = leftRotation;
        this.rightRotation = rightRotation;
    }

    public Integer getId() {
        return id;
    }

    private String getName() {
        return name;
    }

    private Integer getLeftRotation() {
        return leftRotation;
    }

    private Integer getRightRotation() {
        return rightRotation;
    }

    public static Integer getNextDirection(Integer currentDirectionId, String rotation) {
        checkIfDirectionExists(currentDirectionId);

        return Arrays.stream(SpaceProbeDirectionEnum.values())
                .filter(enumDirection -> enumDirection.getId().equals(currentDirectionId))
                .map(filteredEnum -> getRotationDirection(rotation, filteredEnum))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrada nenhuma direção com o id especificado"));
    }

    public static void checkIfDirectionExists(Integer directionId) {
        boolean exists = Arrays.stream(SpaceProbeDirectionEnum.values())
                .anyMatch(enumDirection -> enumDirection.getId().equals(directionId));

        if (Boolean.FALSE.equals(exists)) {
            throw new EntityNotFoundException("Não existe direção com o id: " + directionId);
        }
    }

    private static Integer getRotationDirection(String rotation, SpaceProbeDirectionEnum filteredEnum) {
        if ("LEFT".equals(rotation)) {
            return filteredEnum.getLeftRotation();
        } else if ("RIGHT".equals(rotation)) {
            return filteredEnum.getRightRotation();
        }
        throw new BusinessException("Tipo de rotação inválido");
    }

    public static String getNameById(Integer directionId) {
        checkIfDirectionExists(directionId);

        return Arrays.stream(SpaceProbeDirectionEnum.values())
                .filter(enumDirection -> enumDirection.getId().equals(directionId))
                .map(SpaceProbeDirectionEnum::getName)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrada nenhuma direção com o id especificado"));
    }

    public static SpaceProbeDirectionEnum fromId(Integer id) {
        checkIfDirectionExists(id);

        return Arrays.stream(values())
                .filter(direction -> Objects.equals(direction.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ID de direção inválido: " + id));
    }
}
