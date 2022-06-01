package group2.monopoly.game.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DTO object representing the user's move in a game.
 * @see group2.monopoly
 */
@Data
public class GameInteractionDTO {
    @NotNull
    private Boolean buy;
}
