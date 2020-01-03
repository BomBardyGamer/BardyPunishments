package xyz.bombardydevelopment.bardypunishments.listeners;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;
import xyz.bombardydevelopment.bardypunishments.util.PunishmentUtil;

public class PlayerListeners implements Listener {

    private final BardyPunishments plugin;

    public PlayerListeners(BardyPunishments plugin) {
        this.plugin = plugin;
    }

    public void handlePreLogin(AsyncPlayerPreLoginEvent event) {
        PunishPlayer player = plugin.getDatabase().getPlayerByUUID(event.getUniqueId()).join();
        if (player == null) return;
        if (!PunishmentUtil.hasActivePunishment(player, null, Punishment.PunishmentType.BAN)) return;
        Punishment punishment = PunishmentUtil.getActivePunishment(player, null, Punishment.PunishmentType.BAN);
        ComponentBuilder messageBuilder = new ComponentBuilder("");
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        event.setKickMessage(TextComponent.toLegacyText(messageBuilder.create()));
    }
}
