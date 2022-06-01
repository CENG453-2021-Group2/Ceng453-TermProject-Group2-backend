package group2.monopoly.game.service.engine;

import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.exception.GameOverException;

import java.util.List;
import java.util.Set;

/**
 * Interface exposing the in-game actions.
 * <br><br>
 * The game is played in two-phase steps. These steps are movement and purchase.
 * <br>
 * In movement phase, a pair of dice is rolled and the player moves to a new cell depending on
 * the outcome. In this phase no user interaction is required.
 * <br>
 * In purchase phase, the player can either skip their phase or try to purchase the cell they
 * landed on. After this phase, their turn concludes.
 */
public interface IGameEngine {
    /**
     * Executes the movement step of the player's turn.
     *
     * @param player the player who is in the movement phase of their turn
     * @throws GameOverException if the player goes bankrupt
     */
    void moveStep(Player player) throws GameOverException;

    /**
     * Executes the purchase step of the player's turn.
     *
     * @param player the player who is in the purchase step of their turn
     * @throws GameFaultyMoveException if the player can't purchase the current cell.
     */
    void purchaseStep(Player player) throws GameFaultyMoveException;

    /**
     * Decreases the player's money amount.
     *
     * @param player the player to be charged
     * @param game the game
     * @param cost the amount of money to be charged
     * @throws GameOverException if the player goes bankrupt
     */
    void chargePlayer(Player player, Game game, Integer cost) throws GameOverException;

    /**
     * Charges the player with the port rent if it does not belong to the player.
     *
     * @param player the player that landed on a purchased port
     * @param game the game
     * @param playerOwnedPurchasables the player's owned purchasables
     * @throws GameOverException if the player goes bankrupt
     */
    void handlePortCell(Player player,
                        Game game,
                        List<Integer> portIndices,
                        Set<Integer> playerOwnedPurchasables) throws GameOverException;

    /**
     * Charges the player with the property rent if it does not belong to the player.
     *
     * @param player the player that landed on a purchased property
     * @param game the game
     * @param playerOwnedPurchasables the player's owned purchasables
     * @throws GameOverException if the player goes bankrupt
     */
    void handlePropertyCell(Player player,
                            Game game,
                            Set<Integer> playerOwnedPurchasables) throws GameOverException;

    /**
     * Charges user for landing on the income tax cell
     *
     * @param player the player landed on income tax cell
     * @param game the game
     * @throws GameOverException if the player goes bankrupt
     */
    void handleIncomeTaxCell(Player player, Game game) throws GameOverException;

    /**
     * Sends a player to jail.
     *
     * @param player the player
     */
    void handleGoToJail(Player player);

    /**
     * Checks whether the given player of the given game can purchase the given location.
     *
     * @param player   the player
     * @param game     the ongoing game
     * @param location the index of the cell the player wants to buy
     * @return true if the player can buy the current cell
     */
    boolean canBuy(Player player, Game game, Integer location);

    /**
     * Afflicts the player with crippling debt.
     *
     * @param player the victim of the crippling debt.
     * @param game   the game
     * @throws GameOverException if the player goes bankrupt (they certainly will)
     */
    void nukeGame(Player player, Game game) throws GameOverException;

}
