package group2.monopoly.game.service.manager;

import group2.monopoly.auth.entity.User;
import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.GameTableConfiguration;
import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameManagementException;
import group2.monopoly.game.repository.GameRepository;
import group2.monopoly.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameManagerService {
    private final ICellSequenceGenerator cellSequenceGenerator;
    private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;

    @Autowired
    public GameManagerService(ICellSequenceGenerator cellSequenceGenerator,
                              GameRepository gameRepository, PlayerRepository playerRepository) {
        this.cellSequenceGenerator = cellSequenceGenerator;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    private void populateIndexes(GameTableConfiguration table, CellSequence sequence) {
        ArrayList<Integer> portIndices = sequence.getPortIndexes();
        table.setPortIndices(portIndices);

        ArrayList<Integer> propertyIndices = sequence.getPropertyIndexes();
        table.setPropertyIndices(propertyIndices);

        table.setIncomeTaxIndex(sequence.getIncomeTax());
    }

    public Game createGame(@NotNull User user, @NotNull String name) throws GameManagementException {
        CellSequence cellSequence = cellSequenceGenerator.generateCellSequence();
        if (gameRepository.existsByName(name)) {
            throw new GameManagementException("game with name " + name + " exists");
        }

        GameTableConfiguration table = new GameTableConfiguration();
        populateIndexes(table, cellSequence);
        Game game = Game
                .builder()
                .owner(user)
                .name(name)
                .gameTableConfiguration(table)
                .build();
        Player player = Player
                .builder()
                .game(game)
                .user(user)
                .money(1500)
                .turnOrder(0)
                .build();
        Player computer = Player
                .builder()
                .game(game)
                .user(null)
                .money(1500)
                .turnOrder(1)
                .build();
        game.getPlayers().add(player);
        game.getPlayers().add(computer);
        gameRepository.save(game);
        return game;
    }

    public void deleteGame(@NotNull User user, @NotNull Game game) throws GameManagementException {
        if (!game.getOwner().equals(user)) {
            throw new GameManagementException("should be owner of the game to be able to delete it");
        } else if (game.getCompletionDate() != null) {
            throw new GameManagementException("can not delete already completed games");
        }
        gameRepository.delete(game);
    }

    public List<Game> getAvailableGames(User user) {
        return gameRepository.findUnfinishedGamesByUser(user);
    }

    public Game getGame(User user, Long gameId) throws GameManagementException {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        return optionalGame.filter(g -> g.getPlayers().stream().anyMatch(p -> p.getUser().equals(user))).orElseThrow(() -> new GameManagementException("user is not a player of this game"));
    }
}
