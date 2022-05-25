package group2.monopoly.game.service;

import group2.monopoly.game.entity.GameTableConfiguration;

public interface IGameCellPrice {
    Integer getPropertyPrice(GameTableConfiguration table, Integer tableIndex);

    Integer getPortPrice(GameTableConfiguration table, Integer tableIndex);

    Integer getIncomeTax();

    Integer getSalary();

    Integer getCellPrice(GameTableConfiguration table, Integer tableIndex);

    Integer getPropertyRent(GameTableConfiguration table, Integer tableIndex);

    Integer getPortRent(GameTableConfiguration table, Integer tableIndex, Integer portCount);
}
