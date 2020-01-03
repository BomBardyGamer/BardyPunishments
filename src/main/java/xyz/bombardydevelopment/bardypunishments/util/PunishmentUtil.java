package xyz.bombardydevelopment.bardypunishments.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;

import java.util.stream.Stream;

@UtilityClass
public class PunishmentUtil {

    private BardyPunishments plugin;

    public boolean hasActivePunishment(@NotNull PunishPlayer player, @Nullable PunishPlayer by, @NotNull Punishment.PunishmentType type) {
        return getAllActivePunishments(player)
                .filter(punishment -> by == null || punishment.getExecutor() == by)
                .anyMatch(punishment -> punishment.getType() == type);
    }

    public Punishment getActivePunishment(@NotNull PunishPlayer player, @Nullable PunishPlayer by, @NotNull Punishment.PunishmentType type) {
        return getAllActivePunishments(player)
                .filter(punishment -> by == null || punishment.getExecutor() == by)
                .filter(punishment -> punishment.getType() == type)
                .findFirst()
                .orElse(null);
    }

    private Stream<Punishment> getAllPunishments(@NotNull PunishPlayer player) {
        return player.getPunishments().stream();
    }

    private Stream<Punishment> getAllActivePunishments(@NotNull PunishPlayer player) {
        return getAllPunishments(player)
                .filter(punishment -> !punishment.isExpired());
    }
}
