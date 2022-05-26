package group2.monopoly.game.repository;

import group2.monopoly.auth.entity.User;
import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("select p from player p where p.game = ?1 order by p.turnOrder")
    List<Player> findAllByGameOrderByTurnOrder(Game game);

    @Query("select p from player p inner join game g where g.completionDate is not null and g.completionDate > ?1 and g.completionDate < ?2 order by p.score desc")
    List<Game> findAllByGameNotNull(Date start, Date end);
}
