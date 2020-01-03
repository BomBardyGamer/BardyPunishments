package xyz.bombardydevelopment.bardypunishments.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;

public class PlayerListeners implements Listener {

    private final BardyPunishments plugin;

    public PlayerListeners(BardyPunishments plugin) {
        this.plugin = plugin;
    }

    public void handlePreLogin(AsyncPlayerPreLoginEvent event) {
        PunishPlayer player = plugin.getDatabase().getPlayerByUUID(event.getUniqueId()).join();
    }
}
