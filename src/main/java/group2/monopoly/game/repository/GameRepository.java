package group2.monopoly.game.repository;

import group2.monopoly.auth.entity.User;
import group2.monopoly.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("select g from game g inner join g.players player inner join player.user u where u = " +
           ":user and g.completionDate is null")
    List<Game> findUnfinishedGamesByUser(@Param("user") User user);

    @Query("select g from game g inner join g.players player inner join player.user u where u = " +
           ":user")
    List<Game> findGamesByUser(@Param("user") User user);

    @Query("select g from game g inner join g.players player inner join player.user u where u = " +
           ":user and g.completionDate is not null")
    List<Game> findFinishedGamesBy(@Param("user") User user);

    List<Game> findAllByOwnerAndCompletionDateIsNotNull(User owner);

    Boolean existsByName(String name);
}
