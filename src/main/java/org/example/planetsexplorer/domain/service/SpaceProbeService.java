package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.Planet;
import org.example.planetsexplorer.domain.model.SpaceProbe;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.SpaceProbeRepository;
import org.example.planetsexplorer.shared.dto.CreateSpaceProbeDto;
import org.example.planetsexplorer.shared.dto.SpaceProbeActionDto;
import org.example.planetsexplorer.shared.dto.SpaceProbeResponseDto;
import org.example.planetsexplorer.shared.enums.RoleName;
import org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum;
import org.example.planetsexplorer.shared.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SpaceProbeService {

    private final SpaceProbeRepository spaceProbeRepository;
    private final UserService userService;
    private final PlanetService planetService;

    @Autowired
    public SpaceProbeService(SpaceProbeRepository spaceProbeRepository,
                             UserService userService, PlanetService planetService) {
        this.spaceProbeRepository = spaceProbeRepository;
        this.userService = userService;
        this.planetService = planetService;
    }

    @Transactional(readOnly = true)
    public SpaceProbeResponseDto findById(Integer id) {
        SpaceProbe spaceProbe = spaceProbeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrada nenhuma sonda com o id informado"));

        return new SpaceProbeResponseDto(
                spaceProbe.getId(),
                spaceProbe.getX(),
                spaceProbe.getY(),
                formatNameWithId(getDirectionName(spaceProbe), spaceProbe.getId()),
                getCurrentPlanetName(spaceProbe.getCurrentPlanet()),
                spaceProbe.getOwnerId(),
                spaceProbe.getCreatorId(),
                spaceProbe.getCreatedAt(),
                spaceProbe.getUpdatedAt(),
                spaceProbe.getUpdaterId()
        );
    }

    @Transactional(readOnly = true)
    public Page<SpaceProbeResponseDto> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        return spaceProbeRepository.findAll(pageable)
                .map(probe -> new SpaceProbeResponseDto(probe.getId(),
                        probe.getX(),
                        probe.getY(),
                        formatNameWithId(getDirectionName(probe), probe.getId()),
                        getCurrentPlanetName(probe.getCurrentPlanet()),
                        probe.getOwnerId(),
                        probe.getCreatorId(),
                        probe.getCreatedAt(),
                        probe.getUpdatedAt(),
                        probe.getUpdaterId()
                ));
    }

    public SpaceProbeActionDto moveSpaceProbe(SpaceProbeActionDto spaceProbeActionDto,
                                              String userEmail) {

        String commandsSequence = spaceProbeActionDto.getCommandsSequence().replace(" ", "").toUpperCase();
        SpaceProbe spaceProbe = spaceProbeRepository.findById(spaceProbeActionDto.getSpaceProbeId())
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrada nenhuma sonda com o id informado"));

        validateConditionsToExecuteCommands(userEmail, commandsSequence, spaceProbe);
        executeCommands(commandsSequence, spaceProbe);

        if (!spaceProbe.coordinatesAreInsidePlanetBorders()) {
            spaceProbeActionDto.formatResponseMessage(spaceProbe, true);
            spaceProbe.unlinkPlanet();
            spaceProbeRepository.save(spaceProbe);
            return spaceProbeActionDto;
        }

        validateProbesCollision(spaceProbe);

        spaceProbeRepository.save(spaceProbe);
        spaceProbeActionDto.formatResponseMessage(spaceProbe, false);
        return spaceProbeActionDto;
    }

    private void validateConditionsToExecuteCommands(String userEmail, String commandsSequence, SpaceProbe spaceProbe) {
        validateCommandsSequence(commandsSequence);

        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário relacionado ao token informado"));
        validateUserPermissions(user, spaceProbe);

        if (spaceProbe.getCurrentPlanet() == null) {
            throw new BusinessException("Não é possível movimentar uma sonda que não está em um planeta. Envie-a para um planeta primeiro");
        }
    }

    private void validateCommandsSequence(String commandsSequence) {
        boolean isValid = Pattern.compile("^[MLR]+$")
                .matcher(commandsSequence)
                .matches();

        if (!isValid) {
            throw new BusinessException("A sequência de comandos informada é inválida."
                    + " Deve conter somente as letras 'M', 'L' e 'R'");
        }
    }

    private void validateUserPermissions(User user, SpaceProbe spaceProbe) {
        if (!user.isOwnerOf(spaceProbe) && user.hasRole(RoleName.ROLE_BASIC)) {
            throw new BusinessException("Você não tem permissão de mover ou pousar a sonda de outro usuário");
        }
    }

    private void executeCommands(String commandsSequence, SpaceProbe spaceProbe) {
        commandsSequence.chars().forEach(commandChar -> {
            switch (commandChar) {
                case 'M':
                    spaceProbe.moveForward();
                    break;
                case 'L':
                    spaceProbe.leftRotate();
                    break;
                case 'R':
                    spaceProbe.rightRotate();
                    break;
                default:
                    throw new BusinessException("O comando informado é inválido. Deve ser uma das seguintes letras: 'M', 'L' e 'R'");
            }
        });
    }

    private void validateProbesCollision(SpaceProbe spaceProbe) {
        spaceProbeRepository.findByCurrentPlanet_IdAndXAndYAndIdNot(spaceProbe.getCurrentPlanetId(),
                                                                   spaceProbe.getX(),
                                                                   spaceProbe.getY(),
                                                                   spaceProbe.getId())
        .ifPresent(probe -> {
            throw new BusinessException("Já existe uma sonda nessa posição");
        });
    }

    @Transactional
    public SpaceProbeResponseDto createSpaceProbe(CreateSpaceProbeDto createSpaceProbeDto, String userDetails) {
        User creator = userService.findByEmail(userDetails)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o creatorId informado"));

        Planet planet = null;
        if (createSpaceProbeDto.currentPlanetId() != null) {
            planet = planetService.findPlanetById(createSpaceProbeDto.currentPlanetId())
                    .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum planeta com o id informado"));
        }

        if (createSpaceProbeDto.directionId() != null) {
            SpaceProbeDirectionEnum.checkIfDirectionExists(createSpaceProbeDto.directionId());
        }

        User owner = creator;
        if (createSpaceProbeDto.ownerId() != null) {
            if (creator.hasRole(RoleName.ROLE_BASIC)) {
                throw new BusinessException("Você não tem permissão de criar sondas para outros usuários");
            }
            owner = userService.findById(createSpaceProbeDto.ownerId());
        }

        if (createSpaceProbeDto.x() != null && createSpaceProbeDto.y() != null &&
            createSpaceProbeDto.directionId() != null && createSpaceProbeDto.currentPlanetId() != null) {
            spaceProbeRepository.findByCurrentPlanet_IdAndXAndY(createSpaceProbeDto.currentPlanetId(),
                                                                createSpaceProbeDto.x(),
                                                                createSpaceProbeDto.y())
            .ifPresent(probe -> {
                throw new BusinessException("Já existe uma sonda nessa posição");
            });
        }

        SpaceProbe newSpaceProbe = new SpaceProbe(createSpaceProbeDto.x(),
                                                  createSpaceProbeDto.y(),
                                                  createSpaceProbeDto.directionId(),
                                                  planet,
                                                  owner,
                                                  creator,
                                                  LocalDateTime.now(),
                                                  LocalDateTime.now(),
                                                  creator);

        SpaceProbe spaceProbe = spaceProbeRepository.save(newSpaceProbe);

        return new SpaceProbeResponseDto(spaceProbe.getId(),
                                         spaceProbe.getX(),
                                         spaceProbe.getY(),
                                         formatNameWithId(getDirectionName(spaceProbe),
                                                          spaceProbe.getDirection()),
                                         getCurrentPlanetName(spaceProbe.getCurrentPlanet()),
                                         spaceProbe.getOwnerId(),
                                         creator.getId(),
                                         spaceProbe.getCreatedAt(),
                                         spaceProbe.getUpdatedAt(),
                                         creator.getId());
    }

    private String formatNameWithId(String name, Integer id) {
        return (name == null || id == null) ? null : name + " - ID: " + id;
    }

    private String getDirectionName(SpaceProbe spaceProbe) {
        return spaceProbe.getDirection() == null
                ? null
                : SpaceProbeDirectionEnum.getNameById(spaceProbe.getDirection());
    }

    private String getCurrentPlanetName(Planet currentPlanet) {
        return currentPlanet != null
                ? formatNameWithId(currentPlanet.getName(),
                                   currentPlanet.getId())
                : null;
    }

    public SpaceProbeResponseDto updateSpaceProbeOwner(Integer id,
                                                       CreateSpaceProbeDto createSpaceProbeDto,
                                                       String userDetails) {
        SpaceProbe spaceProbe = spaceProbeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrada nenhuma sonda com o id informado"));

        User updater = userService.findByEmail(userDetails)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado nenhum usuário com o updaterId informado"));

        User owner = userService.findById(createSpaceProbeDto.ownerId());

        spaceProbe.setOwner(owner);
        spaceProbe.setUpdatedAt(LocalDateTime.now());
        spaceProbe.setUpdater(updater);

        spaceProbeRepository.save(spaceProbe);

        return new SpaceProbeResponseDto(spaceProbe.getId(),
                                         spaceProbe.getX(),
                                         spaceProbe.getY(),
                                         formatNameWithId(getDirectionName(spaceProbe),
                                                          spaceProbe.getId()),
                                         getCurrentPlanetName(spaceProbe.getCurrentPlanet()),
                                         spaceProbe.getOwnerId(),
                                         spaceProbe.getCreatorId(),
                                         spaceProbe.getCreatedAt(),
                                         spaceProbe.getUpdatedAt(),
                                         updater.getId());
    }

    public SpaceProbeResponseDto landSpaceProbe(Integer id,
                                                CreateSpaceProbeDto createSpaceProbeDto,
                                                String userEmail) {
        SpaceProbe spaceProbe = findSpaceProbeById(id);
        Planet planet = findPlanetById(createSpaceProbeDto.currentPlanetId());
        Integer direction = SpaceProbeDirectionEnum.fromId(createSpaceProbeDto.directionId()).getId();
        User updater = findUserByEmail(userEmail);
        validateUserPermissions(updater, spaceProbe);

        List<Integer> position = List.of(createSpaceProbeDto.x(), createSpaceProbeDto.y());

        if (position.getFirst() > planet.getWidth() || position.getLast() > planet.getHeight()) {
            throw new BusinessException("A posição especificada está fora do território deste planeta");
        }

        Set<List<Integer>> occupiedPositions =
                planetService.findPlanetOccupiedPositions(createSpaceProbeDto.currentPlanetId())
                        .stream()
                        .map(pos -> List.of(pos.getX(), pos.getY()))
                        .collect(Collectors.toSet());

        if (occupiedPositions.size() == planet.getMaximumOccupancy()) {
            throw new BusinessException("O planeta " + planet.getName() + " já está lotado.");
        }

        if (occupiedPositions.contains(position)) {
            throw new BusinessException("Já existe uma sonda nessa posição");
        }

        updateSpaceProbe(spaceProbe, position, direction, planet, updater);

        return buildSpaceProbeResponse(spaceProbe, updater);
    }

    private SpaceProbe findSpaceProbeById(Integer id) {
        return spaceProbeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sonda não encontrada com id: " + id));
    }

    private Planet findPlanetById(Integer planetId) {
        return planetService.findPlanetById(planetId)
                .orElseThrow(() -> new EntityNotFoundException("Planeta não encontrado com id: " + planetId));
    }

    private User findUserByEmail(String userEmail) {
        return userService.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com email: " + userEmail));
    }

    private void updateSpaceProbe(SpaceProbe spaceProbe, List<Integer> position,
                                  Integer direction, Planet planet, User updater) {
        spaceProbe.setX(position.getFirst());
        spaceProbe.setY(position.getLast());
        spaceProbe.setDirection(direction);
        spaceProbe.setCurrentPlanet(planet);
        spaceProbe.setUpdatedAt(LocalDateTime.now());
        spaceProbe.setUpdater(updater);
        spaceProbeRepository.save(spaceProbe);
    }

    private SpaceProbeResponseDto buildSpaceProbeResponse(SpaceProbe spaceProbe, User updater) {
        return new SpaceProbeResponseDto(
                spaceProbe.getId(),
                spaceProbe.getX(),
                spaceProbe.getY(),
                formatNameWithId(getDirectionName(spaceProbe), spaceProbe.getId()),
                getCurrentPlanetName(spaceProbe.getCurrentPlanet()),
                spaceProbe.getOwnerId(),
                spaceProbe.getCreatorId(),
                spaceProbe.getCreatedAt(),
                spaceProbe.getUpdatedAt(),
                updater.getId()
        );
    }
}
