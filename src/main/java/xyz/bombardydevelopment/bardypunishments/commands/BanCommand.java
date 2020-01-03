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
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BanCommand implements CommandExecutor {

    private final BardyPunishments plugin;

    public BanCommand(BardyPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            CompletableFuture<@Nullable PunishPlayer> player = plugin.getDatabase().getPlayerByName(args[0]);
            UUID uuid;
            if (sender instanceof Player) uuid = ((Player) sender).getUniqueId();
            else uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
            CompletableFuture<@Nullable PunishPlayer> dbSender = plugin.getDatabase().getPlayerByUUID(uuid);
            player.thenCombine(dbSender, (target, executor) -> {
                if (target != null) {
                    sender.sendMessage(ChatColor.GREEN + args[0] + " successfully banned!");
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
                        if (permanent) {
                            StringSubstitutor.replace(this.plugin.getConfig().getString("banned_permanent"), ImmutableMap.<String, Object>builder().put("date", punishment.getUntil()).put("id", punishment.getId()).put("type", punishment.getType()).put("reason", punishment.getReason()).put("punisher", punishment.getExecutor().getHistory().iterator().next().getName()).put("header", this.plugin.getConfig().getString("banned_header")).build());
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }
}
