package group2.monopoly.auth.repository;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.entity.UserPasswordReset;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for storing temporary user password reset tokens.
 */
public interface UserPasswordResetRepository extends JpaRepository<UserPasswordReset, Long> {
    Optional<UserPasswordReset> findFirstByUserOrderByValidUntilDesc(User user);

    Optional<UserPasswordReset> findUserPasswordResetByTokenEquals(@NonNull UUID token);

    void deleteAllByUser(User user);
}
