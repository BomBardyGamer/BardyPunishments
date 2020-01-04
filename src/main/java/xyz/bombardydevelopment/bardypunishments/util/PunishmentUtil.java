package xyz.bombardydevelopment.bardypunishments.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;

import java.util.stream.Stream;

/**
 * Used for getting specific punishments from the database
 *
 * @author BomBardyGamer
 * @since 1.0
 */
@UtilityClass
public class PunishmentUtil {

    private BardyPunishments plugin;

    /**
     * Checks if the specified {@code PunishPlayer} has an active {@code Punishment}
     *
     * @param player the player who is being checked for an active punishment
     * @param by the executor of the punishment that the player is being checked for
     * @param type the {@code PunishmentType} of punishment that the player is being checked for
     * @return if the database returns any results (if the player has a punishment)
     */
    public boolean hasActivePunishment(@NotNull PunishPlayer player, @Nullable PunishPlayer by, @NotNull Punishment.PunishmentType type) {
        return getAllActivePunishments(player)
                .filter(punishment -> by == null || punishment.getExecutor() == by)
                .anyMatch(punishment -> punishment.getType() == type);
    }

    /**
     * Fetches an active {@code Punishment} from the database matching the specified arguments
     *
     * @param player the {@code PunishPlayer} who has been punished
     * @param by the {@code PunishPlayer} who executed the punishment
     * @param type the {@code PunishmentType} of the punishment being searched for
     * @return the {@code Punishment} that was found, or else null
     */
    public Punishment getActivePunishment(@NotNull PunishPlayer player, @Nullable PunishPlayer by, @NotNull Punishment.PunishmentType type) {
        return getAllActivePunishments(player)
                .filter(punishment -> by == null || punishment.getExecutor() == by)
                .filter(punishment -> punishment.getType() == type)
                .findFirst()
                .orElse(null);
    }

    /**
     * Fetches all punishments of the {@code PunishPlayer} from the database
     *
     * @param player the player who's punishments are being fetched
     * @return a {@code Stream<Punishment>} of punishments found in the database, or else null
     */
    private Stream<Punishment> getAllPunishments(@NotNull PunishPlayer player) {
        return player.getPunishments().stream();
    }

    /**
     * Fetches all active punishments of the {@code PunishPlayer} from the database
     *
     * @param player the player who's active punishments are being fetched
     * @return a {@code Stream<Punishment>} of active punishments found in the database, or else null
     */
    private Stream<Punishment> getAllActivePunishments(@NotNull PunishPlayer player) {
        return getAllPunishments(player)
                .filter(punishment -> !punishment.isExpired());
    }
}
