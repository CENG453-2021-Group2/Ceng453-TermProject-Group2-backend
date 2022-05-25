package group2.monopoly.game.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GameTableConfiguration {
    @NotNull
    @Column(name="income_tax_index", nullable = false)
    private Integer incomeTaxIndex;

    @ElementCollection
    private List<Integer> propertyIndices = new ArrayList<>();

    @ElementCollection
    private List<Integer> portIndices = new ArrayList<>();
}
