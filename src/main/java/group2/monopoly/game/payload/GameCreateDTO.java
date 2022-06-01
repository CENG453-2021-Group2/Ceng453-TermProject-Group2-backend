package group2.monopoly.game.payload;

import group2.monopoly.auth.entity.User;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DTO object for creating a new game.
 * @see group2.monopoly.game.service.manager.GameManagerService#createGame(User, String)
 */
@Data
public class GameCreateDTO {
    @NotNull
    private String name;
}
