package xyz.bombardydevelopment.bardypunishments.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;

import java.util.concurrent.CompletableFuture;

public class UnbanCommand implements CommandExecutor {

    private final BardyPunishments plugin;

    public UnbanCommand(BardyPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            CompletableFuture<@Nullable PunishPlayer> player = plugin.getDatabase().getPlayerByName(args[0]);
            player.thenAccept(punishPlayer -> {
                if (punishPlayer == null) return;
                Punishment punishment1 = punishPlayer.getPunishments().stream()
                        .filter(punishment -> !punishment.isExpired())
                        .filter(punishment -> punishment.getType() == Punishment.PunishmentType.BAN)
                        .findFirst().orElse(null);
                if (punishment1 == null) return;
                punishment1.setExpired(true);
                plugin.getDatabase().savePunishment(punishment1);
            });
            sender.sendMessage(ChatColor.GREEN + args[0] + " successfully unbanned!");
            return true;
        }
        return false;
    }
}
