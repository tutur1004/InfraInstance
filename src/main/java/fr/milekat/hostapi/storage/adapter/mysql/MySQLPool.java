package fr.milekat.hostapi.storage.adapter.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLPool {
    private static HikariDataSource ds;

    public MySQLPool(@NotNull FileConfiguration config) {
        HikariConfig hConfig = new HikariConfig();
        if (config.getString("storage.type").equalsIgnoreCase("mysql")) {
            hConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            hConfig.setJdbcUrl( "jdbc:mysql://" +
                    config.getString("storage.mysql.hostname") + "/" +
                    config.getString("storage.mysql.database"));
        } else if (config.getString("storage.type").equalsIgnoreCase("mariadb")) {
            hConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            hConfig.setJdbcUrl( "jdbc:mariadb://" +
                    config.getString("storage.mysql.hostname") + "/" +
                    config.getString("storage.mysql.database"));
        }
        hConfig.setUsername(config.getString("storage.mysql.username"));
        hConfig.setPassword(config.getString("storage.mysql.password"));
        hConfig.addDataSourceProperty("cachePrepStmts", "true");
        hConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(hConfig);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void disconnect() {
        ds.close();
    }
}
