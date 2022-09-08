package fr.milekat.hostapi.api.classes;

import java.util.Date;

public class Instance {
    private final Integer id;
    private String name;
    private String serverId;
    private final String description;
    private String message;
    private String hostname;
    private int port;
    private InstanceState state;
    private final Game game;
    private final User user;
    private Date creation;
    private Date deletion = null;

    public Instance(String name, String serverId) {
        this.id = null;
        this.name = name;
        this.description = null;
        this.serverId = serverId;
        this.game = null;
        this.user = null;
    }

    public Instance(String name, Game game, User user) {
        this.id = null;
        this.name = name;
        this.description = "Server created by: " + user.getLastName();
        this.game = game;
        this.user = user;
    }

    public Instance(Integer id, String name, String serverId, String description, String message, String hostname,
                    int port, InstanceState state, Game game, User user, Date creation, Date deletion) {
        this.id = id;
        this.name = name;
        this.serverId = serverId;
        this.description = description;
        this.message = message;
        this.hostname = hostname;
        this.port = port;
        this.state = state;
        this.game = game;
        this.user = user;
        this.creation = creation;
        this.deletion = deletion;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerId() {
        return serverId;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InstanceState getState() {
        return state;
    }

    public void setState(InstanceState state) {
        this.state = state;
    }

    public Game getGame() {
        return game;
    }

    public User getUser() {
        return user;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Date getDeletion() {
        return deletion;
    }

    public void setDeletion(Date deletion) {
        this.deletion = deletion;
    }
}