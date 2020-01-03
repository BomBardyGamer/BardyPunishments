package xyz.bombardydevelopment.bardypunishments.commands;

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

public class KickCommand implements CommandExecutor {

    private final BardyPunishments plugin;

    public KickCommand(BardyPunishments plugin) {
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
                if (target != null && Bukkit.getPlayer(target.getUuid()) != null) {
                    sender.sendMessage(ChatColor.GREEN + args[0] + " successfully kicked!");
                    return new Punishment(target, executor, null, "", Punishment.PunishmentType.KICK);
                } else if (target != null) {
                    sender.sendMessage(ChatColor.RED + "This player is not online!");
                    return null;
                } else {
                    sender.sendMessage(ChatColor.RED + "This player does not exist!");
                    return null;
                }
            }).thenAccept(punishment -> {
                if (punishment != null) {
                    plugin.getDatabase().savePunishment(punishment);
                }
            });
            return true;
        }
        return false;
    }
}
