package group2.monopoly.auth.repository;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.entity.UserPasswordReset;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for storing temporary user password reset tokens.
 */
public interface UserPasswordResetRepository extends JpaRepository<UserPasswordReset, Long> {
    Optional<UserPasswordReset> findFirstByUserOrderByValidUntilDesc(User user);

    Optional<UserPasswordReset> findUserPasswordResetByTokenEquals(@NonNull UUID token);

    @Transactional
    void deleteAllByUser(User user);
}
