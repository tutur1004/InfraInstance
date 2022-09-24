package fr.milekat.infra.api.classes;

import java.util.Date;
import java.util.Map;

public class Game {
    private final Integer id;
    private final String name;
    private final Date create;
    private final boolean enable;
    private final String gameVersion;
    private final String serverVersion;
    private final String image;
    private final int requirements;
    private final String icon;
    private final Map<String, String> configs;

    public Game(Integer id, String name, Date create, boolean enable, String gameVersion, String serverVersion,
                String image, int requirements, String icon, Map<String, String> configs) {
        this.id = id;
        this.name = name;
        this.create = create;
        this.enable = enable;
        this.gameVersion = gameVersion;
        this.serverVersion = serverVersion;
        this.image = image;
        this.requirements = requirements;
        this.icon = icon;
        this.configs = configs;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreate() {
        return create;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getImage() {
        return image;
    }

    public int getRequirements() {
        return requirements;
    }

    public String getIcon() {
        return icon;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }
}
