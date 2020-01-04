package xyz.bombardydevelopment.bardypunishments.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bombardydevelopment.bardypunishments.BardyPunishments;
import xyz.bombardydevelopment.bardypunishments.orm.PunishHistory;
import xyz.bombardydevelopment.bardypunishments.orm.PunishPlayer;
import xyz.bombardydevelopment.bardypunishments.orm.Punishment;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used for initialising database tables
 *
 * @author BomBardyGamer
 * @since 1.0
 */
public class DatabaseWrapper {

    private final ConnectionSource connectionSource;
    private final Dao<PunishPlayer, Integer> playerDao;
    private final Dao<Punishment, Integer> punishmentDao;
    private final Dao<PunishHistory, Integer> historyDao;
    private BardyPunishments plugin = new BardyPunishments();

    public DatabaseWrapper() throws SQLException {
        String databaseUrl = "jdbc:" + plugin.getConfig().getString("sql.driver").toLowerCase() + "://" + plugin.getConfig().getString("sql.address") + "/" + plugin.getConfig().getString("sql.database");
        this.connectionSource = new JdbcConnectionSource(databaseUrl, plugin.getConfig().getString("sql.username"), plugin.getConfig().getString("sql.password"), DatabaseTypeUtils.createDatabaseType(databaseUrl));
        TableUtils.createTableIfNotExists(connectionSource, PunishPlayer.class);
        TableUtils.createTableIfNotExists(connectionSource, Punishment.class);
        TableUtils.createTableIfNotExists(connectionSource, PunishHistory.class);
        this.playerDao = DaoManager.createDao(connectionSource, PunishPlayer.class);
        this.punishmentDao = DaoManager.createDao(connectionSource, Punishment.class);
        this.historyDao = DaoManager.createDao(connectionSource, PunishHistory.class);
    }

    /**
     * Fetches a {@code CompletableFuture<@Nullable PunishPlayer>} object from the database
     * from their {@code UUID}
     *
     * @param uuid the {@code UUID} of the {@code PunishPlayer} being fetched
     * @return a {@code CompletableFuture<@Nullable PunishPlayer>} object of the player from
     * the database, or else null
     */
    public CompletableFuture<@Nullable PunishPlayer> getPlayerByUUID(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return playerDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fetches a {@code CompletableFuture<@Nullable PunishPlayer>} object from the database
     * from their username
     * @param name the username of the player
     * @return a {@code CompletableFuture<@Nullable PunishPlayer>} object of the player from
     * the database, or else null
     */
    public CompletableFuture<@Nullable PunishPlayer> getPlayerByName(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PunishHistory player = historyDao.queryBuilder().orderBy("time", false).where().eq("name", name).queryForFirst();
                if (player != null) {
                    return playerDao.queryForSameId(player.getPlayer());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Saves a {@code Punishment} object to the database
     *
     * @param punishment the {@code Punishment} object to be saved
     */
    public void savePunishment(@NotNull Punishment punishment) {
        try {
            punishmentDao.createOrUpdate(punishment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
