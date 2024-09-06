package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.Planet;
import org.example.planetsexplorer.domain.model.Role;
import org.example.planetsexplorer.domain.model.SpaceProbe;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.SpaceProbeRepository;
import org.example.planetsexplorer.impl.PlanetOccupiedPositionsProjectionImpl;
import org.example.planetsexplorer.shared.dto.CreateSpaceProbeDto;
import org.example.planetsexplorer.shared.dto.SpaceProbeActionDto;
import org.example.planetsexplorer.shared.dto.SpaceProbeResponseDto;
import org.example.planetsexplorer.shared.enums.RoleName;
import org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum;
import org.example.planetsexplorer.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaceProbeServiceTest {

    @InjectMocks
    private SpaceProbeService spaceProbeService;

    @Mock
    private SpaceProbeRepository spaceProbeRepository;

    @Mock
    private UserService userService;

    @Mock
    private PlanetService planetService;

    @Test
    void findByIdShouldReturnSpaceProbeWhenIdExists() {
        User owner = createUser();
        User creator = new User();
        owner.setId(4);
        Planet planet = createPlanet();

        SpaceProbe spaceProbe = createSpaceProbe(planet, owner);
        spaceProbe.setCreator(creator);
        spaceProbe.setCreatedAt(LocalDateTime.now());
        spaceProbe.setUpdatedAt(LocalDateTime.now());
        spaceProbe.setUpdater(creator);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));

        SpaceProbeResponseDto response = spaceProbeService.findById(1);

        assertNotNull(response);
        assertEquals(spaceProbe.getId(), response.id());
        assertEquals(spaceProbe.getX(), response.x());
        assertEquals(spaceProbe.getY(), response.y());
        assertEquals("Norte - ID: 1", response.direction());
        assertEquals("PLANETAX - ID: 1", response.currentPlanet());
        assertEquals(spaceProbe.getOwnerId(), response.ownerId());
        assertEquals(spaceProbe.getCreatorId(), response.creatorId());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        assertEquals(spaceProbe.getUpdaterId(), response.updaterId());
    }

    @Test
    void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        when(spaceProbeRepository.findById(99)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.findById(99));
    }

    @Test
    void findAllShouldReturnAllSpaceProbesPaged() {
        User owner = createUser();
        User creator = new User();
        owner.setId(4);
        Planet planet = createPlanet();

        SpaceProbe spaceProbe = createSpaceProbe(planet, owner);
        spaceProbe.setCreator(creator);
        spaceProbe.setCreatedAt(LocalDateTime.now().minusDays(1));
        spaceProbe.setUpdatedAt(LocalDateTime.now());
        spaceProbe.setUpdater(creator);

        Page<SpaceProbe> spaceProbePage = new PageImpl<>(List.of(spaceProbe));

        when(spaceProbeRepository.findAll(PageRequest.of(0, 10))).thenReturn(spaceProbePage);

        Page<SpaceProbeResponseDto> responsePage = spaceProbeService.findAll(0, 10);

        assertNotNull(responsePage);
        assertNotNull(responsePage.getContent().getFirst());
    }

    @Test
    void moveSpaceProbeShouldExecuteCommandsAndReturnInsidePlanetMessage() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(spaceProbeRepository.findByCurrentPlanet_IdAndXAndYAndIdNot(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("r", 1, null);
        SpaceProbeActionDto response = spaceProbeService.moveSpaceProbe(actionDto, "user@example.com");

        assertNotNull(response);
        assertNotNull(spaceProbe.getCurrentPlanet());
        assertNotNull(spaceProbe.getX());
        assertNotNull(spaceProbe.getY());
        assertNotNull(spaceProbe.getDirection());
        assertTrue(response.getResponseMessage().contains("Posição final da sonda de id " + spaceProbe.getId()
                + ": x=" + spaceProbe.getX() + " y=" + spaceProbe.getY()
                + " apontando para " + SpaceProbeDirectionEnum.getNameById(spaceProbe.getDirection())));
    }

    @Test
    void moveSpaceProbeShouldExecuteCommandsAndReturnOutsidePlanetMessage() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("mmrmmmmmm", 1, null);
        SpaceProbeActionDto response = spaceProbeService.moveSpaceProbe(actionDto, "user@example.com");

        assertNotNull(response);
        assertNull(spaceProbe.getCurrentPlanet());
        assertNull(spaceProbe.getX());
        assertNull(spaceProbe.getY());
        assertNull(spaceProbe.getDirection());
        assertTrue(response.getResponseMessage().contains("A sonda de id " + spaceProbe.getId()
                + " saiu do território do planeta " + planet.getName()
                + ". Para voltar a movimentar essa sonda é necessário enviá-la a um planeta"));
    }

    @Test
    void moveSpaceProbeShouldThrowEntityNotFoundExceptionWhenSpaceProbeIdDoesNotExist() {
        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("MMRMMMMMMMMMM", 99, null);

        when(spaceProbeRepository.findById(99)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.moveSpaceProbe(actionDto, "user@example.com"));
    }

    @Test
    void moveSpaceProbeShouldThrowBusinessExceptionWhenCommandsSequenceDoesNotMatchRegex() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("alsdjaslkçdm", 1, null);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));

        assertThrows(BusinessException.class, () -> spaceProbeService.moveSpaceProbe(actionDto, "user@example.com"));
    }

    @Test
    void moveSpaceProbeShouldThrowEntityNotFoundExceptionWhenUserEmailDoesNotExist() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("MMRMMMMMMMMMM", 1, null);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail(anyString())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.moveSpaceProbe(actionDto, "emailnaoexiste@example.com"));
    }

    @Test
    void moveSpaceProbeShouldThrowBusinessExceptionWhenUserIsNotOwnerOfTheSpaceProbeAndHasBasicAccess() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);
        User anotherUser = new User();
        anotherUser.setId(8);
        spaceProbe.setOwner(anotherUser);

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("MMRMMMMMMMMMM", 1, null);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> spaceProbeService.moveSpaceProbe(actionDto, "user@example.com"));
    }

    @Test
    void moveSpaceProbeShouldMoveWhenUserIsNotOwnerOfTheSpaceProbeButHasAdminAccess() {
        Planet planet = createPlanet();
        User user = createUser();
        Role role = new Role();
        role.setName(RoleName.ROLE_ADMIN);
        user.setRole(role);
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);
        User anotherUser = new User();
        anotherUser.setId(8);
        spaceProbe.setOwner(anotherUser);

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("MMRMMMMMMMMMM", 1, null);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        SpaceProbeActionDto spaceProbeActionDto = spaceProbeService.moveSpaceProbe(actionDto, "user@example.com");

        assertNotNull(spaceProbeActionDto.getResponseMessage());
    }

    @Test
    void moveSpaceProbeShouldThrowBusinessExceptionWhenSpaceProbeIsNotAtAPlanet() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);
        spaceProbe.setCurrentPlanet(null);

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("MMRMMMMMMMMMM", 1, null);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> spaceProbeService.moveSpaceProbe(actionDto, "user@example.com"));
    }

    @Test
    void moveSpaceProbeShouldThrowBusinessExceptionWhenProbesCollide() {
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        SpaceProbeActionDto actionDto = new SpaceProbeActionDto("L", 1, null);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(spaceProbeRepository.findByCurrentPlanet_IdAndXAndYAndIdNot(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenThrow(new BusinessException("a"));

        assertThrows(BusinessException.class, () -> spaceProbeService.moveSpaceProbe(actionDto, "user@example.com"));
    }

    @Test
    void createSpaceProbeShouldReturnCreatedProbeWhenNothingInformed() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(null, null, null, null, null);
        User creator = createUser();

        SpaceProbe newSpaceProbe = new SpaceProbe();
        newSpaceProbe.setId(1);
        newSpaceProbe.setOwner(creator);
        newSpaceProbe.setCreator(creator);
        newSpaceProbe.setCreatedAt(LocalDateTime.now());
        newSpaceProbe.setUpdatedAt(LocalDateTime.now());
        newSpaceProbe.setUpdater(creator);

        when(userService.findByEmail("creator@example.com")).thenReturn(Optional.of(creator));
        when(spaceProbeRepository.save(any(SpaceProbe.class))).thenReturn(newSpaceProbe);

        SpaceProbeResponseDto response = spaceProbeService.createSpaceProbe(createDto, "creator@example.com");

        assertNotNull(response);
        assertEquals(newSpaceProbe.getId(), response.id());
        assertNull(response.x());
        assertNull(response.y());
        assertNull(response.direction());
        assertNull(response.currentPlanet());
        assertEquals(newSpaceProbe.getOwnerId(), response.ownerId());
        assertEquals(newSpaceProbe.getCreatorId(), response.creatorId());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        assertEquals(newSpaceProbe.getUpdaterId(), response.updaterId());
    }

    @Test
    void createSpaceProbeShouldReturnCreatedProbeWhenEverythingIsInformed() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 8, 2, 2, 4);
        User creator = createUser();
        Role role = new Role();
        role.setName(RoleName.ROLE_ADMIN);
        creator.setRole(role);
        Planet planet = createPlanet();

        SpaceProbe newSpaceProbe = new SpaceProbe();
        newSpaceProbe.setId(1);
        newSpaceProbe.setX(10);
        newSpaceProbe.setY(8);
        newSpaceProbe.setDirection(2);
        newSpaceProbe.setCurrentPlanet(planet);
        newSpaceProbe.setOwner(creator);
        newSpaceProbe.setCreator(creator);
        newSpaceProbe.setCreatedAt(LocalDateTime.now());
        newSpaceProbe.setUpdatedAt(LocalDateTime.now());
        newSpaceProbe.setUpdater(creator);

        when(userService.findByEmail("creator@example.com")).thenReturn(Optional.of(creator));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(planet));
        when(spaceProbeRepository.save(any(SpaceProbe.class))).thenReturn(newSpaceProbe);
        when(userService.findById(anyInt())).thenReturn(new User());

        SpaceProbeResponseDto response = spaceProbeService.createSpaceProbe(createDto, "creator@example.com");

        assertNotNull(response);
        assertEquals(newSpaceProbe.getId(), response.id());
        assertEquals(newSpaceProbe.getX(), response.x());
        assertEquals(newSpaceProbe.getY(), response.y());
        assertEquals("Leste - ID: " + newSpaceProbe.getDirection(), response.direction());
        assertEquals(newSpaceProbe.getCurrentPlanetName() + " - ID: " + newSpaceProbe.getCurrentPlanetId(), response.currentPlanet());
        assertEquals(newSpaceProbe.getOwnerId(), response.ownerId());
        assertEquals(newSpaceProbe.getCreatorId(), response.creatorId());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        assertEquals(newSpaceProbe.getUpdaterId(), response.updaterId());
    }

    @Test
    void createSpaceProbeShouldThrowEntityNotFoundExceptionWhenEmailDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 8, 2, 2, 4);

        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.createSpaceProbe(createDto, "creator@example.com"));
    }

    @Test
    void createSpaceProbeShouldThrowEntityNotFoundExceptionWhenPlanetDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 8, 2, 2, 4);

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.createSpaceProbe(createDto, "creator@example.com"));
    }

    @Test
    void createSpaceProbeShouldThrowEntityNotFoundExceptionWhenDirectionIdDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 8, 99, 2, 4);

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(new Planet()));

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.createSpaceProbe(createDto, "creator@example.com"));
    }

    @Test
    void createSpaceProbeShouldThrowBusinessExceptionWhenOwnerIdIsInformedAndUserHasBasicAccess() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 8, 2, 2, 4);
        User user = createUser();

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(new Planet()));

        assertThrows(BusinessException.class, () -> spaceProbeService.createSpaceProbe(createDto, "creator@example.com"));
    }

    @Test
    void createSpaceProbeShouldThrowBusinessExceptionWhenPositionIsOccupied() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 8, 2, 2, 4);
        User user = createUser();
        Role role = new Role();
        role.setName(RoleName.ROLE_ADMIN);
        user.setRole(role);

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(new Planet()));
        when(spaceProbeRepository.findByCurrentPlanet_IdAndXAndY(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.of(new SpaceProbe()));

        assertThrows(BusinessException.class, () -> spaceProbeService.createSpaceProbe(createDto, "creator@example.com"));
    }

    @Test
    void updateSpaceProbeOwnerShouldReturnUpdatedUserWhenOwnerIdExists() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(null, null, null, null, 2);
        SpaceProbe spaceProbe = createSpaceProbe(createPlanet(), createUser());

        User updater = new User();
        updater.setId(3);
        updater.setEmail("updater@example.com");

        User newOwner = new User();
        newOwner.setId(2);

        when(spaceProbeRepository.findById(1)).thenReturn(Optional.of(spaceProbe));
        when(userService.findByEmail("updater@example.com")).thenReturn(Optional.of(updater));
        when(userService.findById(2)).thenReturn(newOwner);

        SpaceProbeResponseDto response = spaceProbeService.updateSpaceProbeOwner(1, createDto, "updater@example.com");

        assertNotNull(response);
        assertEquals(newOwner.getId(), response.ownerId());
        assertEquals(updater.getId(), response.updaterId());
        assertNotNull(response.updatedAt());
    }

    @Test
    void updateSpaceProbeOwnerShouldThrowEntityNotFoundExceptionWhenSpaceProbeDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(null, null, null, null, 2);

        when(spaceProbeRepository.findById(anyInt())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.updateSpaceProbeOwner(99, createDto, "updater@example.com"));
    }

    @Test
    void updateSpaceProbeOwnerShouldThrowEntityNotFoundExceptionWhenUserEmailDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(null, null, null, null, 2);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(new SpaceProbe()));
        when(userService.findByEmail(anyString())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.updateSpaceProbeOwner(1, createDto, "naoexiste@example.com"));
    }

    @Test
    void updateSpaceProbeOwnerShouldThrowEntityNotFoundExceptionWhenOwnerIdDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(null, null, null, null, 99);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(new SpaceProbe()));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(userService.findById(anyInt())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.updateSpaceProbeOwner(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldReturnLandedSpaceProbeWhenValidParameters() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 1, 1, null);
        Planet planet = createPlanet();
        User updater = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, updater);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(spaceProbe));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(planet));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(updater));
        when(planetService.findPlanetOccupiedPositions(anyInt())).thenReturn(Collections.emptyList());

        SpaceProbeResponseDto response = spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com");

        assertNotNull(response);
        assertEquals(10, response.x());
        assertEquals(9, response.y());
        assertEquals("Norte - ID: " + spaceProbe.getDirection(), response.direction());
        assertEquals(spaceProbe.getCurrentPlanetName() + " - ID: " + spaceProbe.getCurrentPlanetId(), response.currentPlanet());
    }

    @Test
    void landSpaceProbeShouldThrowEntityNotFoundExceptionWhenSpaceProbeDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 1, 1, null);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldThrowEntityNotFoundExceptionWhenPlanetDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 1, 1, null);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(new SpaceProbe()));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldThrowEntityNotFoundExceptionWhenDirectionIdDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 99, 1, null);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(new SpaceProbe()));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(new Planet()));

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldThrowEntityNotFoundExceptionWhenUserEmailDoesNotExist() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 1, 1, null);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(new SpaceProbe()));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(new Planet()));
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldThrowBusinessExceptionWhenUserIsNotOwnerOfTheSpaceProbeAndHasBasicAccess() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 1, 1, null);

        User anotherUser = new User();
        anotherUser.setId(90);

        SpaceProbe spaceProbe = new SpaceProbe();
        spaceProbe.setOwner(anotherUser);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(spaceProbe));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(new Planet()));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(createUser()));

        assertThrows(BusinessException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldReturnLandedSpaceProbeWhenUserIsNotOwnerOfTheSpaceProbeButHasAdminAccess() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 9, 1, 1, null);

        User anotherUser = new User();
        anotherUser.setId(90);

        User user = createUser();
        Role role = new Role();
        role.setName(RoleName.ROLE_ADMIN);
        user.setRole(role);

        Planet planet = createPlanet();
        SpaceProbe spaceProbe = createSpaceProbe(planet, anotherUser);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(spaceProbe));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(planet));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        SpaceProbeResponseDto landedSpaceProbe = spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com");

        assertNotNull(landedSpaceProbe);
    }

    @Test
    void landSpaceProbeShouldThrowBusinessExceptionWhenPositionIsOutsidePlanet() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(11, 11, 1, 1, null);
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(spaceProbe));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(planet));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldThrowBusinessExceptionWhenPlanetIsFull() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 10, 1, 1, null);
        Planet planet = createPlanet();
        planet.setMaximumOccupancy(1);
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(spaceProbe));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(planet));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(planetService.findPlanetOccupiedPositions(anyInt())).thenReturn(List.of(new PlanetOccupiedPositionsProjectionImpl(9, 9)));

        assertThrows(BusinessException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    @Test
    void landSpaceProbeShouldThrowBusinessExceptionWhenPositionsIsOccupied() {
        CreateSpaceProbeDto createDto = new CreateSpaceProbeDto(10, 10, 1, 1, null);
        Planet planet = createPlanet();
        User user = createUser();
        SpaceProbe spaceProbe = createSpaceProbe(planet, user);

        when(spaceProbeRepository.findById(anyInt())).thenReturn(Optional.of(spaceProbe));
        when(planetService.findPlanetById(anyInt())).thenReturn(Optional.of(planet));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(planetService.findPlanetOccupiedPositions(anyInt())).thenReturn(List.of(new PlanetOccupiedPositionsProjectionImpl(10, 10)));

        assertThrows(BusinessException.class, () -> spaceProbeService.landSpaceProbe(1, createDto, "updater@example.com"));
    }

    private Planet createPlanet() {
        Planet planet = new Planet();
        planet.setId(1);
        planet.setName("PLANETAX");
        planet.setWidth(10);
        planet.setHeight(10);
        planet.setMaximumOccupancy(100);
        return planet;
    }

    private User createUser() {
        Role role = new Role();
        role.setName(RoleName.ROLE_BASIC);

        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setRole(role);
        return user;
    }

    private SpaceProbe createSpaceProbe(Planet planet, User user) {
        SpaceProbe spaceProbe = new SpaceProbe();
        spaceProbe.setId(1);
        spaceProbe.setX(10);
        spaceProbe.setY(10);
        spaceProbe.setDirection(1);
        spaceProbe.setCurrentPlanet(planet);
        spaceProbe.setOwner(user);
        spaceProbe.setCreator(user);
        return spaceProbe;
    }
}