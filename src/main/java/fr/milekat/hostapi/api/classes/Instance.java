package fr.milekat.hostapi.api.classes;

import java.util.Date;

public class Instance {
    private final Integer id;
    private String name;
    private String serverId;
    private final String description;
    private String message;
    private int port;
    private InstanceState state;
    private final Game game;
    private final User host;
    private Date creation;
    private Date deletion = null;

    public Instance(String name, String serverId) {
        this.id = null;
        this.name = name;
        this.description = null;
        this.serverId = serverId;
        this.game = null;
        this.host = null;
    }

    public Instance(String name, Game game, User host) {
        this.id = null;
        this.name = name;
        this.description = "Server created by: " + host.getLastName();
        this.game = game;
        this.host = host;
    }

    public Instance(Integer id, String name, String serverId, String description, String message, int port, InstanceState state, Game game, User host, Date creation, Date deletion) {
        this.id = id;
        this.name = name;
        this.serverId = serverId;
        this.description = description;
        this.message = message;
        this.port = port;
        this.state = state;
        this.game = game;
        this.host = host;
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

    public User getHost() {
        return host;
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
