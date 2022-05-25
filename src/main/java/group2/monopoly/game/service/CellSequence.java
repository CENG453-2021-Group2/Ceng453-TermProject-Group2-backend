package group2.monopoly.game.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CellSequence {
    private ArrayList<Integer> propertyIndexes;
    private ArrayList<Integer> portIndexes;
    private Integer incomeTax;
}
