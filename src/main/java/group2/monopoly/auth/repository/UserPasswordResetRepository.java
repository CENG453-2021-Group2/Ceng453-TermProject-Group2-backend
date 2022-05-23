package group2.monopoly.auth.repository;

import group2.monopoly.auth.model.User;
import group2.monopoly.auth.model.UserPasswordReset;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface UserPasswordResetRepository extends JpaRepository<UserPasswordReset, Long> {
    Optional<UserPasswordReset> findUserPasswordResetByUser(User user);
    Optional<UserPasswordReset> findUserPasswordResetByUserAndValidUntilBefore(User user, @NonNull Date validUntil);
    Optional<UserPasswordReset> findUserPasswordResetByToken(@NonNull UUID token);

    Boolean existsByUser(User user);
    Boolean existsByUserAndValidUntilBefore(User user, @NonNull Date validUntil);

    void deleteAllByUser(User user);
}
