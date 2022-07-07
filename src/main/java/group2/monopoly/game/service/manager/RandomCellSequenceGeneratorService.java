package group2.monopoly.game.service.manager;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Implements {@link ICellSequenceGenerator} by creating randomly generated cell sequences.
 */
@Service
public class RandomCellSequenceGeneratorService implements ICellSequenceGenerator {

    /**
     * Generates a {@link CellSequence} randomly.
     *
     * @return the randomly generated cell sequence
     */
    @Override
    public CellSequence generateCellSequence() {
        CellSequence cellSequence = new CellSequence();
        int remainingIncomeTax = 1;
        int remainingProperty = 8;
        int remainingFerry = 4;
        int total = remainingIncomeTax + remainingProperty + remainingFerry;

        Random rand = new Random();
        for (int i = 1; i < 16; i++) {

            // Go to jail and Jail
            if (i == 4 || i == 12) {
                continue;
            }

            int nextCellTypeIndex = rand.nextInt(total);

            if (nextCellTypeIndex < remainingIncomeTax) {
                // Income tax
                cellSequence.setIncomeTax(i);
                remainingIncomeTax--;
            } else if (nextCellTypeIndex < remainingIncomeTax + remainingProperty) {
                // Property
                cellSequence.getPropertyIndexes().add(i);
                remainingProperty--;
            } else {
                // Port
                cellSequence.getPortIndexes().add(i);
                remainingFerry--;
            }
            total--;
        }
        return cellSequence;
    }
}
