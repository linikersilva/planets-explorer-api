package org.example.planetsexplorer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.planetsexplorer.domain.model.SpaceProbe;
import org.example.planetsexplorer.domain.model.User;
import org.example.planetsexplorer.domain.repository.SpaceProbeRepository;
import org.example.planetsexplorer.shared.dto.SpaceProbeActionDto;
import org.example.planetsexplorer.shared.enums.RoleName;
import org.example.planetsexplorer.shared.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class SpaceProbeService {

    private final SpaceProbeRepository spaceProbeRepository;
    private final UserService userService;

    @Autowired
    public SpaceProbeService(SpaceProbeRepository spaceProbeRepository,
                             UserService userService) {
        this.spaceProbeRepository = spaceProbeRepository;
        this.userService = userService;
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

        if (spaceProbe.getActualPlanet() == null) {
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
            throw new BusinessException("Você não tem permissão de mover a sonda de outro usuário");
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
        spaceProbeRepository.findByActualPlanet_IdAndXAndYAndIdNot(spaceProbe.getActualPlanetId(),
                                                                   spaceProbe.getX(),
                                                                   spaceProbe.getY(),
                                                                   spaceProbe.getId())
        .ifPresent(probe -> {
            throw new BusinessException("Já existe uma sonda nessa posição");
        });
    }
}
