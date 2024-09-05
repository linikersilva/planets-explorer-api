package org.example.planetsexplorer.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.planetsexplorer.domain.model.SpaceProbe;
import org.example.planetsexplorer.shared.enums.SpaceProbeDirectionEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceProbeActionDto {

    @NotBlank(message = "O atributo commandsSequence não pode ser nulo ou vazio")
    private String commandsSequence;
    @NotNull(message = "O atributo spaceProbeId não pode ser nulo")
    private Integer spaceProbeId;
    private String responseMessage;

    public SpaceProbeActionDto(String commandsSequence,
                               Integer spaceProbeId,
                               String responseMessage) {
        this.commandsSequence = commandsSequence;
        this.spaceProbeId = spaceProbeId;
        this.responseMessage = responseMessage;
    }

    public String getCommandsSequence() {
        return commandsSequence;
    }

    public Integer getSpaceProbeId() {
        return spaceProbeId;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void formatResponseMessage(SpaceProbe spaceProbe, boolean isUnlinkedSpaceProbe) {
        if (isUnlinkedSpaceProbe) {
            this.responseMessage = "A sonda de id " + spaceProbe.getId()
                    + " saiu do território do planeta " + spaceProbe.getCurrentPlanetName()
                    + ". Para voltar a movimentar essa sonda é necessário enviá-la a um planeta";
        } else {
            this.responseMessage = "Posição final da sonda de id " + spaceProbe.getId()
                    + ": x=" + spaceProbe.getX() + " y=" + spaceProbe.getY()
                    + " apontando para " + SpaceProbeDirectionEnum.getNameById(spaceProbe.getDirection());
        }

        this.spaceProbeId = null;
        this.commandsSequence = null;
    }
}
