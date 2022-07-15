package fr.milekat.hostapi.api.classes;

import java.util.Date;

public class Log {
    private final Date logDate;
    private final Instance instance;
    private final LogAction action;
    private final User user;
    private final Game game;

    public Log(Date logDate, Instance instance, LogAction action, User user, Game game) {
        this.logDate = logDate;
        this.instance = instance;
        this.action = action;
        this.user = user;
        this.game = game;
    }

    public Date getLogDate() {
        return logDate;
    }

    public Instance getInstance() {
        return instance;
    }

    public LogAction getAction() {
        return action;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }
}
