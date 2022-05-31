package group2.monopoly.game.service.engine;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiceGeneratorService implements IDiceGenerator {

    @Override
    public List<Integer> roll() {
        Random random = new Random();
        List<Integer> set = new ArrayList<>();
        set.add(random.nextInt(1, 7));
        set.add(random.nextInt(1, 7));
        return set;
    }
}
