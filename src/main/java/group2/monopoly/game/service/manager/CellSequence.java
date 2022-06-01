package group2.monopoly.game.service.manager;

import lombok.*;

import java.util.ArrayList;

/**
 * A utility class supplied by {@link ICellSequenceGenerator} for configuring game tables.
 *
 * @see ICellSequenceGenerator
 * @see GameManagerService
 * @see group2.monopoly.game.entity.Game
 * @see group2.monopoly.game.entity.GameTableConfiguration
 */
@Getter
@Setter
@AllArgsConstructor
public class CellSequence {
    private ArrayList<Integer> propertyIndexes;
    private ArrayList<Integer> portIndexes;
    private Integer incomeTax;

    public CellSequence() {
        propertyIndexes = new ArrayList<>();
        portIndexes = new ArrayList<>();
        incomeTax = 0;
    }
}
