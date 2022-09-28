package fr.milekat.infra.storage.adapter.sql;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.*;
import fr.milekat.infra.storage.Storage;
import fr.milekat.infra.storage.StorageImplementation;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.storage.exeptions.StorageLoaderException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class SQLStorage implements StorageImplementation {
    private final String SCHEMA_FILE = "infra_schema.sql";
    private final SQLDataBaseConnection DB;
    private final String DatabaseName;
    private final String PREFIX = Main.getFileConfig().getString("storage.prefix");
    private final List<String> TABLES = Arrays.asList(PREFIX + "games", PREFIX + "instances", PREFIX + "logs",
            PREFIX + "users", PREFIX + "profiles", PREFIX + "properties", PREFIX + "game_strategies");

    /*
        SQL Queries
     */
    private final String CHECK_TABLE = "SELECT TABLE_NAME " +
            "FROM information_schema.TABLES " +
            "WHERE TABLE_SCHEMA=? AND TABLE_NAME = ?;";

    //  Tables
    private final String STRUCTURE_GAME = "g.`id`, g.`name`, g.`description`, g.`create_date`, " +
            "g.`enable`, g.`version`, g.`image`, g.`requirements`, g.`icon` ";
    private final String STRUCTURE_USER = "u.`id`, u.`uuid`, u.`last_name`, u.`tickets` ";
    private final String STRUCTURE_INSTANCE = "i.`id`, i.`name`, i.`server_id`, " +
            "i.`description`, i.`message`, i.`port`, i.`hostname`, i.`state`, i.`access`, " +
            "i.`game`, i.`user`, i.`creation`, i.`deletion` " + "," + STRUCTURE_GAME  + "," + STRUCTURE_USER;
    private final String STRUCTURE_LOGS = "l.`id`, l.`date`, l.`instance`, l.`action`, l.`user`, l.`game` ";

    //  Queries
    private final String GET_TICKETS = "SELECT tickets " +
            "FROM {prefix}users u " +
            "WHERE u.uuid = ?;";
    private final String GET_GAMES = "SELECT " + STRUCTURE_GAME +
            "FROM {prefix}games g;";
    private final String GET_GAME_ID = "SELECT " + STRUCTURE_GAME +
            "FROM {prefix}games g " +
            "WHERE g.id = ?;";
    private final String SEARCH_GAME = "SELECT " + STRUCTURE_GAME +
            "FROM {prefix}games g " +
            "WHERE g.name = ? AND g.version = ?;";
    private final String GET_USERS = "SELECT " + STRUCTURE_USER +
            "FROM {prefix}users u;";
    private final String GET_USER = "SELECT " + STRUCTURE_USER +
            "FROM {prefix}users u " +
            "WHERE u.last_name = ?;";
    private final String GET_USER_UUID = "SELECT " + STRUCTURE_USER +
            "FROM {prefix}users u " +
            "WHERE u.uuid = ?;";
    private final String GET_ACTIVE_INSTANCES = "SELECT " + STRUCTURE_INSTANCE +
            "FROM {prefix}instances i " +
            "INNER JOIN {prefix}games g ON i.game=g.id " +
            "INNER JOIN {prefix}users u ON i.user=u.id " +
            "WHERE i.state <>4;";
    private final String GET_INSTANCE_WITH_ID = "SELECT " + STRUCTURE_INSTANCE +
            "FROM {prefix}instances i " +
            "INNER JOIN {prefix}games g ON i.game=g.id " +
            "INNER JOIN {prefix}users u ON i.user=u.id " +
            "WHERE i.state <>4 AND i.id = ?;";
    private final String GET_INSTANCE_WITH_NAME = "SELECT " + STRUCTURE_INSTANCE +
            "FROM {prefix}instances i " +
            "INNER JOIN {prefix}games g ON i.game=g.id " +
            "INNER JOIN {prefix}users u ON i.user=u.id " +
            "WHERE i.state <>4 AND i.name = ?;";
    private final String GET_N_LOGS = "SELECT " +
            "FROM {prefix}logs l " +
            "ORDER BY l.log_id DESC LIMIT ?;";
    private final String GET_LOGS_WITHIN_DATE = "SELECT * FROM {prefix}logs l " +
            "INNER JOIN {prefix}instances i ON l.instance = i.id " +
            "INNER JOIN {prefix}users u on l.user = u.id " +
            "INNER JOIN {prefix}games g on l.game = g.id " +
            "WHERE (l.log_date BETWEEN ? AND ?);";
    private final String FIND_AVAILABLE_PORTS = "SELECT port " +
            "FROM {prefix}instances i " +
            "WHERE i.state <>4;";
    private final String FETCH_GAME_CONFIGS = "SELECT props.name as var, props.value as val " +
            "FROM {prefix}properties props " +
            "INNER JOIN {prefix}profiles prof ON props.profile=prof.id " +
            "LEFT JOIN {prefix}game_strategies str ON props.profile=str.profile " +
            "WHERE props.enable=1 AND (prof.name='global' OR (str.game=? AND prof.enable=1)) " +
            "ORDER BY props.profile DESC;";

    private final String ADD_TICKETS = "UPDATE {prefix}users u" +
            "SET u.tickets = u.tickets + ? " +
            "WHERE u.uuid=?;";
    private final String REMOVE_TICKETS = "UPDATE {prefix}users u" +
            "SET u.tickets = u.tickets - ? " +
            "WHERE u.uuid=?;";
    private final String CREATE_GAME = "INSERT INTO {prefix}games" +
            "(name, enable, image, requirements) " +
            "VALUES (?,?,?,?);";
    private final String CREATE_INSTANCE = "INSERT INTO {prefix}instances" +
            "(name, description, port, game, user) VALUES (?,?,?,?,?);";

    private final String UPDATE_GAME = "UPDATE {prefix}games " +
            "SET name=?, enable=?, image=?, requirements=?";
    private final String UPDATE_INSTANCE_FULL = "UPDATE {prefix}instances " +
            "SET name=?, server_id=?, description=?, message=?, " +
            "hostname=?, port=?, state=? " +
            "WHERE id = ?";
    private final String UPDATE_INSTANCE_NAME = "UPDATE {prefix}instances " +
            "SET name=? " +
            "WHERE id = ?";
    private final String UPDATE_INSTANCE_STATE = "UPDATE {prefix}instances " +
            "SET state=?, access=? " +
            "WHERE id = ?";
    private final String UPDATE_INSTANCE_ADDRESS = "UPDATE {prefix}instances " +
            "SET hostname=?, port=? " +
            "WHERE id = ?";
    private final String UPDATE_INSTANCE_CREATION = "UPDATE {prefix}instances " +
            "SET creation=? " +
            "WHERE id = ?";
    private final String UPDATE_INSTANCE_DELETION = "UPDATE {prefix}instances " +
            "SET deletion=? " +
            "WHERE id = ?";
    private final String UPDATE_USER = "UPDATE {prefix}users " +
            "SET uuid=?, last_name=?, tickets=? " +
            "WHERE uuid=?;";
    private final String UPDATE_CREATE_USER = "INSERT INTO {prefix}users" +
            "(uuid, last_name) " +
            "VALUES (?,?) ON DUPLICATE KEY UPDATE last_name = ?;";

    public SQLStorage(@NotNull FileConfiguration config) throws StorageLoaderException {
        DatabaseName = config.getString("storage.sql.database");
        DB = new SQLConnection(config).getSqlDataBaseConnection();
        try {
            if (!checkStorages()) {
                applySchema();
            }
        } catch (StorageExecuteException | IOException exception) {
            throw new StorageLoaderException("Unsupported database type");
        }
    }

    /**
     * Format query by replacing {prefix} with {@link SQLStorage#PREFIX}
     */
    @Contract(pure = true)
    private @NotNull String formatQuery(@NotNull String query) {
        return query.replaceAll("\\{prefix}", PREFIX);
    }

    @Override
    public String getImplementationName() {
        return DB.getImplementationName();
    }

    /**
     * Disconnect from HikariCP pool
     */
    @Override
    public void disconnect() {
        DB.close();
    }

    /**
     * Apply SQL Default schema with infra_schema.sql dump
     */
    private void applySchema() throws IOException, StorageLoaderException {
        List<String> statements;
        //  Read schema file
        try (InputStream schemaFileIS = this.getClass().getResourceAsStream(SCHEMA_FILE)) {
            if (schemaFileIS == null) {
                throw new StorageLoaderException("Missing schema file");
            }
            statements = SQLUtils.getQueries(schemaFileIS).stream()
                    .map(this::formatQuery)
                    .collect(Collectors.toList());
        }
        //  Apply Schema
        try (Connection connection = DB.getConnection();
             Statement s = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (String query : statements) {
                s.addBatch(query);
            }
            s.executeBatch();
        } catch (Exception exception) {
            if (!exception.getMessage().contains("already exists") && Main.DEBUG) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Check if all tables are created
     * @return true if all tables are created
     */
    @Override
    public boolean checkStorages() throws StorageExecuteException {
        try (Connection connection = DB.getConnection()) {
            for (String table : TABLES) {
                try (PreparedStatement q = connection.prepareStatement(formatQuery(CHECK_TABLE))) {
                    q.setString(1, DatabaseName);
                    q.setString(2, table);
                    q.execute();
                    if (!q.getResultSet().next()) {
                        if (Main.DEBUG) {
                            Main.getOwnLogger().warning("Table: " + table + " not found in " + DatabaseName);
                        }
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, "Missing schema file");
        }
    }

    /*
        Tickets
     */

    /**
     * Query tickets for this player
     * @param uuid player uuid
     * @return amount of reaming ticket
     */
    @Override
    public Integer getTicket(@NotNull UUID uuid) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_TICKETS))) {
            q.setString(1, uuid.toString());
            q.execute();
            if (q.getResultSet().next()) {
                return q.getResultSet().getInt("tickets");
            }
            return 0;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Add tickets to this player
     * @param uuid player uuid
     * @param amount amount of tickets to add to this player
     */
    @Override
    public void addPlayerTickets(UUID uuid, Integer amount) throws StorageExecuteException {
        updateUserTickets(uuid, amount, ADD_TICKETS);
    }

    /**
     * Remove tickets to this player
     * @param uuid player uuid
     * @param amount amount of tickets to remove to this player
     */
    @Override
    public void removePlayerTickets(UUID uuid, Integer amount) throws StorageExecuteException {
        updateUserTickets(uuid, amount, REMOVE_TICKETS);
    }

    private void updateUserTickets(@NotNull UUID uuid, Integer amount, String query) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(query))) {
            q.setInt(1, amount);
            q.setString(2, uuid.toString());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Games
     */

    /**
     * Query all games
     * @return list of games
     */
    @Override
    public List<Game> getGames() throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_GAMES))) {
            q.execute();
            List<Game> games = new ArrayList<>();
            while (q.getResultSet().next()) {
                games.add(resultSetToGame(q.getResultSet()));
            }
            games.forEach(game -> Storage.GAMES_CACHE.put(game, new Date()));
            return games;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Get last queried list of games (If list is too old, or not exist, it will re-queried the list)
     * @return "recent" list of games
     */
    @Override
    public List<Game> getGamesCached() throws StorageExecuteException {
        if (Storage.GAMES_CACHE.values().stream()
                .anyMatch(date -> date.getTime() + Storage.GAMES_DELAY < new Date().getTime())) {
            return getGames();
        } else  {
            return new ArrayList<>(Storage.GAMES_CACHE.keySet());
        }
    }

    /**
     * Get a games by id
     * @return game or null if not exist
     */
    @Override
    public Game getGame(int id) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_GAME_ID))) {
            q.setInt(1, id);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToGame(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }    }

    /**
     * Query a games by name
     * @return game or null if not exist
     */
    @Override
    public Game getGame(String gameName, String version) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(SEARCH_GAME))) {
            q.setString(1, gameName);
            q.setString(2, version);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToGame(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Create a new game
     */
    @Override
    public void createGame(Game game) throws StorageExecuteException {
        gameQuery(game, CREATE_GAME);
    }

    /**
     * Update an existing game
     */
    @Override
    public void updateGame(Game game) throws StorageExecuteException {
        gameQuery(game, UPDATE_GAME);
    }

    private void gameQuery(@NotNull Game game, String query) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(query))) {
            q.setString(1, game.getName());
            q.setBoolean(2, game.isEnable());
            q.setString(3, game.getImage());
            q.setInt(4, game.getRequirements());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Instance
     */

    /**
     * Query active instances
     * @return list of instances
     */
    @Override
    public List<Instance> getActiveInstances() throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_ACTIVE_INSTANCES))) {
            q.execute();
            List<Instance> instances = new ArrayList<>();
            while (q.getResultSet().next()) {
                instances.add(resultSetToInstance(q.getResultSet()));
            }
            instances.forEach(instance -> Storage.INSTANCES_CACHE.put(instance, new Date()));
            return instances;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Get last queried list of active instances (If cache is too old, or not exist, it will re-queried the list)
     * @return list of instances
     */
    @Override
    public List<Instance> getActiveInstancesCached() throws StorageExecuteException {
        if (Storage.INSTANCES_CACHE.values().stream()
                .anyMatch(date -> date.getTime() + Storage.INSTANCES_DELAY < new Date().getTime())) {
            return getActiveInstances();
        } else  {
            return new ArrayList<>(Storage.INSTANCES_CACHE.keySet());
        }
    }

    /**
     * Query an instance
     * @param id of instance
     * @return instance (If exist)
     */
    @Override
    public @Nullable Instance getInstance(int id) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_INSTANCE_WITH_ID))) {
            q.setInt(1, id);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToInstance(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Query an instance
     * @param name of instance
     * @return instance (If exist)
     */
    @Override
    public @Nullable Instance getInstance(String name) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_INSTANCE_WITH_NAME))) {
            q.setString(1, name);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToInstance(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public Instance createInstance(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(CREATE_INSTANCE))) {
            q.setString(1, instance.getName());
            q.setString(2, instance.getDescription());
            q.setInt(3, instance.getPort());
            q.setInt(4, instance.getGame().getId());
            q.setInt(5, instance.getUser().getId());
            q.execute();
            return getInstance(instance.getName());
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstance(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE_FULL))) {
            q.setString(1, instance.getName());
            q.setString(2, instance.getServerId());
            q.setString(3, instance.getDescription());
            q.setString(4, instance.getMessage());
            q.setString(5, instance.getHostname());
            q.setInt(6, instance.getPort());
            q.setInt(7, instance.getState().getStateId());
            q.setInt(8, instance.getId());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstanceName(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE_NAME))) {
            q.setString(1, instance.getName());
            q.setInt(2, instance.getId());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstanceState(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE_STATE))) {
            q.setInt(1, instance.getState().getStateId());
            q.setInt(2, instance.getAccess().getAccessId());
            q.setInt(3, instance.getId());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstanceAddress(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE_ADDRESS))) {
            q.setString(1, instance.getHostname());
            q.setInt(2, instance.getPort());
            q.setInt(3, instance.getId());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstanceCreation(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE_CREATION))) {
            if (instance.getCreation()==null) return;
            q.setTimestamp(1, new Timestamp(instance.getCreation().getTime()));
            q.setInt(2, instance.getId());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstanceDeletion(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE_DELETION))) {
            if (instance.getDeletion()==null) return;
            q.setTimestamp(1, new Timestamp(instance.getDeletion().getTime()));
            q.setInt(2, instance.getId());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public @Nullable Integer findAvailablePort(List<Integer> ports) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(FIND_AVAILABLE_PORTS))) {
            q.execute();
            List<Integer> usedPorts = new ArrayList<>();
            while (q.getResultSet().next()) {
                usedPorts.add(q.getResultSet().getInt("i.port"));
            }
            for (Integer port : ports) {
                if (!usedPorts.contains(port)) {
                    return port;
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Users
     */
    /**
     * Query all users ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @return all users
     */
    @Override
    public List<User> getUsers() throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_USERS))) {
            q.execute();
            List<User> users = new ArrayList<>();
            while (q.getResultSet().next()) {
                users.add(resultSetToUser(q.getResultSet()));
            }
            return users;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Query a user by his name if present, otherwise return null
     * @param name of player
     * @return User or null
     */
    @Override
    public @Nullable User getUser(String name) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_USER))) {
            q.setString(1, name);
            q.execute();
            if (q.getResultSet().next()) {
                User user = resultSetToUser(q.getResultSet());
                Storage.USERS_CACHE.put(user, new Date());
                return user;
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Query a user if present, otherwise return null
     * @param uuid of player
     * @return User or null
     */
    @Override
    public @Nullable User getUser(@NotNull UUID uuid) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_USER_UUID))) {
            q.setString(1, uuid.toString());
            q.execute();
            if (q.getResultSet().next()) {
                User user = resultSetToUser(q.getResultSet());
                Storage.USERS_CACHE.put(user, new Date());
                return user;
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Get a user if present from cache, otherwise try to query the user
     * @param uuid of player
     * @return User or null
     */
    @Override
    public @Nullable User getUserCache(UUID uuid) throws StorageExecuteException {
        Optional<Map.Entry<User, Date>> optionalUser = Storage.USERS_CACHE.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getUuid().equals(uuid))
                .filter(entry -> entry.getValue().getTime() + Storage.USERS_DELAY > new Date().getTime())
                .findFirst();
        if (optionalUser.isPresent()) {
            return optionalUser.get().getKey();
        } else  {
            return getUser(uuid);
        }
    }

    /**
     * Create or Update user if exist
     * @param uuid uuid of this user
     * @param username last username known
     */
    @Override
    public void updateUser(@NotNull UUID uuid, String username) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_CREATE_USER))) {
            q.setString(1, uuid.toString());
            q.setString(2, username);
            q.setString(3, username);
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Update user (If exist)
     * @param user profile
     */
    @Override
    public void updateUser(@NotNull User user) throws StorageExecuteException {
        updateUser(user.getUuid(), user.getLastName(), user.getTickets());
    }

    /**
     * Update user (If exist)
     * @param uuid profile
     * @param username last username
     * @param amount of tickets
     */
    @Override
    public void updateUser(@NotNull UUID uuid, String username, Integer amount) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_USER))) {
            q.setString(1, uuid.toString());
            q.setString(2, username);
            q.setInt(3, amount);
            q.setString(4, uuid.toString());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Logs
     */
    /**
     * Retrieve last n logs
     * @param count number of the latest logs to retrieve
     */
    @Override
    public List<Log> getLogs(int count) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_N_LOGS))) {
            q.setInt(1, count);
            q.execute();
            List<Log> logs = new ArrayList<>();
            while (q.getResultSet().next()) {
                logs.add(resultSetToLog(q.getResultSet()));
            }
            return logs;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Retrieve all logs between 2 days ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @param from first date from the period
     * @param to end of the period
     */
    @Override
    public List<Log> getLogs(@NotNull Date from, @NotNull Date to) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_LOGS_WITHIN_DATE))) {
            q.setTimestamp(1, new Timestamp(from.getTime()));
            q.setTimestamp(2, new Timestamp(to.getTime()));
            q.execute();
            List<Log> logs = new ArrayList<>();
            while (q.getResultSet().next()) {
                logs.add(resultSetToLog(q.getResultSet()));
            }
            return logs;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Class shortcuts
     */
    /**
     * Shortcut to convert MySQL instance row into instance class
     */
    @Contract("_ -> new")
    private @NotNull Instance resultSetToInstance(@NotNull ResultSet r) throws SQLException {
        Date creation = null;
        Date deletion = null;
        if (r.getTimestamp("i.creation")!=null) {
            creation = new Date(r.getTimestamp("i.creation").getTime());
        }
        if (r.getTimestamp("i.deletion")!=null) {
            deletion = new Date(r.getTimestamp("i.deletion").getTime());
        }
        return new Instance(r.getInt("i.id"),
                r.getString("i.name"),
                r.getString("i.server_id"),
                r.getString("i.description"),
                r.getString("i.message"),
                r.getString("i.hostname"),
                r.getInt("i.port"),
                InstanceState.fromInteger(r.getInt("i.state")),
                AccessStates.fromInteger(r.getInt("i.access")),
                resultSetToGame(r),
                resultSetToUser(r),
                creation,
                deletion
        );
    }

    /**
     * Shortcut to convert MySQL game row into game class
     */
    @Contract("_ -> new")
    private @NotNull Game resultSetToGame(@NotNull ResultSet r) throws SQLException {
        return new Game(r.getInt("g.id"),
                r.getString("g.name"),
                r.getString("g.description"),
                new Date(r.getTimestamp("g.create_date").getTime()),
                r.getBoolean("g.enable"),
                r.getString("g.version"),
                r.getString("g.image"),
                r.getInt("g.requirements"),
                r.getString("g.icon"),
                fetchConfigs(r.getInt("g.id")));
    }

    /**
     * Shortcut to convert MySQL log row into log class
     */
    @Contract("_ -> new")
    private @NotNull Log resultSetToLog(@NotNull ResultSet r) throws SQLException {
        return new Log(new Date(r.getTimestamp("l.log_date").getTime()),
                resultSetToInstance(r),
                LogAction.fromInteger(r.getInt("l.action")),
                resultSetToUser(r),
                resultSetToGame(r));
    }

    /**
     * Shortcut to convert MySQL user row into user class
     */
    @Contract("_ -> new")
    private @NotNull User resultSetToUser(@NotNull ResultSet r) throws SQLException {
        return new User(r.getInt("u.id"),
                UUID.fromString(r.getString("u.uuid")),
                r.getString("u.last_name"),
                r.getInt("u.tickets"));
    }

    /**
     * Shortcut to fetch all game configs
     */
    private @NotNull Map<String, String> fetchConfigs(int gameId) throws SQLException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(FETCH_GAME_CONFIGS))) {
            q.setInt(1, gameId);
            q.execute();
            Map<String, String> configs = new HashMap<>();
            while (q.getResultSet().next()) {
                configs.put(q.getResultSet().getString("var"), q.getResultSet().getString("val"));
            }
            return configs;
        }
    }
}