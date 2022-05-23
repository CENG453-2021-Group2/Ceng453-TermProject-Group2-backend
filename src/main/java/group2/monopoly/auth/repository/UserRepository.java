package group2.monopoly.auth.repository;

import group2.monopoly.auth.model.User;
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
    Optional<User> findById(Long id);

    Optional<User> findByEmailAndRoleTrue(String email);
    Optional<User> findByUsernameAndRoleTrue(String username);
    Optional<User> findByIdAndRoleTrue(Long id);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}
