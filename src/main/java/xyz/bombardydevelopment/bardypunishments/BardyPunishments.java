package xyz.bombardydevelopment.bardypunishments;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.bombardydevelopment.bardypunishments.database.DatabaseWrapper;

public class BardyPunishments extends JavaPlugin {

    private @Getter DatabaseWrapper database;

    @Override
    public void onEnable() {

    }
}
