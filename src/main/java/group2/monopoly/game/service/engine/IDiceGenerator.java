package group2.monopoly.game.service.engine;

import java.util.List;

/**
 * Interface for generating dice rolls.
 * <br>
 * Each rolled die should have a value between 1 and 6 (both inclusive).
 *
 * @see #roll()
 */
public interface IDiceGenerator {
    /**
     * Rolls dice and returns the result.
     *
     * @return the list of rolled numbers
     */
    List<Integer> roll();
}
