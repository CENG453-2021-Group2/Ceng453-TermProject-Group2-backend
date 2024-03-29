package group2.monopoly.auth.repository;

import group2.monopoly.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for user accounts.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User findUserByUsername(String username);

    Optional<User> findById(Long id);

    Boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
