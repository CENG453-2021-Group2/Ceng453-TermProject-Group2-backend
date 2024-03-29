package group2.monopoly.game.service.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads sequence of dice rolls from a specified file.
 * <br>
 * The path of the file should be specified in 'monopoly.dicefile' property.
 * The dice file should consist of lines with two integers between 1 and 6 in each, separated by
 * a blank space. The generator goes back to the first line after reading the last line.
 *
 * @see FileDiceGenerator
 */
@Slf4j
@Service(value = "fileDiceGenerator")
public class FileDiceGenerator implements IDiceGenerator {

    private List<List<Integer>> diceSequence;

    private int index;

    /**
     * Reads a dice file to generate the dice throw list the generator will iterate over.
     *
     * @param filePath the path of the dice file
     */
    public FileDiceGenerator(@Value("${monopoly.dicefile}") String filePath) {
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            diceSequence = lines
                    .filter(s -> s.matches("^[1-6] [1-6]$"))
                    .map(s -> s.split(" ", 2))
                    .map(words -> List.of(Integer.valueOf(words[0]), Integer.valueOf(words[1])))
                    .toList();
            if (diceSequence.isEmpty()) {
                log.warn("Dice file contains no valid sequence lines, defaulting to 1 - 1.");
                diceSequence = List.of(List.of(1, 1));
            }
            index = 0;
        } catch (IOException e) {
            log.error("Exception occurred while opening dice sequence file " + filePath);
            log.error("pwd " + System.getenv("PWD"));
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            log.error("Exception occurred while parsing dice sequence file " + filePath);
            throw e;
        }
    }

    /**
     * Returns the numbers specified in the line after the previous line.
     *
     * @return rolled numbers
     */
    @Override
    public List<Integer> roll() {
        List<Integer> dice = new ArrayList<>();
        dice.add(diceSequence.get(index).get(0));
        dice.add(diceSequence.get(index).get(1));
        index = (index + 1) % (diceSequence.size());
        return dice;
    }
}
