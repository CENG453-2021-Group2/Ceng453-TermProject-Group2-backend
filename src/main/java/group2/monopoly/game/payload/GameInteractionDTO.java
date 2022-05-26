package group2.monopoly.game.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GameInteractionDTO {
    @NotNull
    private Boolean buy;
}
