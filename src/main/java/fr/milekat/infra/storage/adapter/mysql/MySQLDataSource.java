package fr.milekat.infra.storage.adapter.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLDataSource extends HikariDataSource {
    private static HikariDataSource ds;

    /**
     * Init the data source with bukkit config
     */
    public MySQLDataSource(@NotNull FileConfiguration config) {
        HikariConfig hConfig = new HikariConfig();
        if (config.getString("storage.type").equalsIgnoreCase("mysql")) {
            hConfig.setDriverClassName("com.mysql.jdbc.Driver");
            hConfig.setJdbcUrl( "jdbc:mysql://" + config.getString("storage.mysql.hostname") + ":" +
                    config.getString("storage.mysql.port") + "/" +
                    config.getString("storage.mysql.database"));
        } else if (config.getString("storage.type").equalsIgnoreCase("mariadb")) {
            hConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
            hConfig.addDataSourceProperty("serverName", config.getString("storage.mysql.hostname"));
            hConfig.addDataSourceProperty("portNumber", config.getString("storage.mysql.port"));
            hConfig.addDataSourceProperty("databaseName", config.getString("storage.mysql.database"));
        }
        hConfig.setUsername(config.getString("storage.mysql.username"));
        hConfig.setPassword(config.getString("storage.mysql.password"));
        hConfig.addDataSourceProperty("cachePrepStmts", "true");
        hConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hConfig.addDataSourceProperty("useLocalSessionState", "true");
        hConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hConfig.addDataSourceProperty("maintainTimeStats", "false");
        hConfig.setConnectionTestQuery("SELECT 1");
        ds = new HikariDataSource(hConfig);
    }

    /**
     * Get an established connection of this data source.
     */
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * Shutdown the DataSource and its associated pool.
     */
    public void disconnect() {
        ds.close();
    }
}
