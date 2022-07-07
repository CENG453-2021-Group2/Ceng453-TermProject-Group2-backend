package group2.monopoly.game.service.engine;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for generating pseudo-random dice rolls.
 */
@Service(value = "randomDiceGenerator")
public class RandomDiceGeneratorService implements IDiceGenerator {

    /**
     * Rolls two numbers between 1 and 6.
     *
     * @return random dices
     */
    @Override
    public List<Integer> roll() {
        Random random = new Random();
        List<Integer> set = new ArrayList<>();
        set.add(random.nextInt(1, 7));
        set.add(random.nextInt(1, 7));
        return set;
    }
}
