package group2.monopoly.game.service.manager;

import lombok.*;

import java.util.ArrayList;

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
