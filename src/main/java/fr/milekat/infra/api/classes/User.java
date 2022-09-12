package fr.milekat.infra.api.classes;

import java.util.UUID;

public class User {
    private final Integer id;
    private final UUID uuid;
    private final String lastName;
    private final int tickets;

    public User(Integer id, UUID uuid, String lastName, int tickets) {
        this.id = id;
        this.uuid = uuid;
        this.lastName = lastName;
        this.tickets = tickets;
    }

    public Integer getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastName() {
        return lastName;
    }

    public int getTickets() {
        return tickets;
    }
}
