package group2.monopoly.game.service.manager;

/**
 * Helper interface with a single method {@link #generateCellSequence()} for creating game tables.
 */
public interface ICellSequenceGenerator {
    /**
     * Generates a {@link CellSequence} object.
     *
     * @return the generated cell sequence
     */
    CellSequence generateCellSequence();
}
