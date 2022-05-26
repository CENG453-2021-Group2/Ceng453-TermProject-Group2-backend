package group2.monopoly.game.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GameCreateDTO {
    @NotNull
    private String name;
}
