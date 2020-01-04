package xyz.bombardydevelopment.bardypunishments.commands;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishHistory;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;
import xyz.bombardydevelopment.bardypunishments.util.TimeUtil;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BanCommand implements CommandExecutor {

    private final BardyPunishments plugin;

    public BanCommand(BardyPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;
        CompletableFuture<@Nullable PunishPlayer> player = plugin.getDatabase().getPlayerByName(args[0]);
        UUID uuid;
        if (sender instanceof Player) uuid = ((Player) sender).getUniqueId();
        else uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        CompletableFuture<@Nullable PunishPlayer> dbSender = plugin.getDatabase().getPlayerByUUID(uuid);
        if (args.length < 3) {
            player.thenCombine(dbSender, (target, executor) -> {
                if (target != null) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("command.ban.broadcast_ban")));
                    if (args[1] != null)
                        return new Punishment(target, executor, new Date(System.currentTimeMillis() + TimeUtil.convertDuration(args[1])), "", Punishment.PunishmentType.BAN);
                    return new Punishment(target, executor, null, "", Punishment.PunishmentType.BAN);
                }
                sender.sendMessage(ChatColor.RED + "This player has never joined before!");
                return null;
            }).thenAccept(punishment -> {
                if (punishment != null) {
                    plugin.getDatabase().savePunishment(punishment);
                    Player player1 = Bukkit.getPlayer(punishment.getPlayer().getUuid());
                    if (player1 != null) {
                        boolean permanent = punishment.getUntil() == null;
                        String formattedMessage = StringSubstitutor.replace(
                                this.plugin.getConfig().getString(permanent ? "banned_permanent" : "banned_temporary"),
                                ImmutableMap.<String, Object>builder()
                                        .put("header", StringSubstitutor.replace(
                                                this.plugin.getConfig().getString("banned_header"),
                                                ImmutableMap.<String, Object>builder()
                                                        .put("type", punishment.getType())
                                                        .put("date", punishment.getTime())
                                                        .put("until", punishment.getUntil())
                                                        .put("duration", TimeUtil.convertTime(punishment.getUntil().getTime() - punishment.getTime().getTime()))
                                                        .put("reason", punishment.getReason())
                                                        .put("executor", punishment.getExecutor().getHistory().stream().max(Comparator.comparing(PunishHistory::getTime)).get().getName())
                                                        .put("executor_uuid", punishment.getExecutor().getUuid())
                                                        .build()))
                                        .put("date", punishment.getTime())
                                        .put("duration", TimeUtil.convertTime(punishment.getUntil().getTime() - punishment.getTime().getTime()))
                                        .put("until", punishment.getUntil())
                                        .build());
                        Bukkit.getPlayer(args[0]).kickPlayer(formattedMessage);
                    }
                }
            });
            return true;
        }
        return false;
    }
}
