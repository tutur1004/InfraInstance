package fr.milekat.infra.api.classes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

// TODO: 28/09/2022 V2: Split games and versions features !
public class Game {
    private final Integer id;
    private final String name;
    private final String description;
    private final Date create;
    private final boolean enable;
    private final String version;
    private final String image;
    private final int requirements;
    private final String icon;
    private final Map<String, String> configs;

    public Game(Integer id, String name, String description, Date create, boolean enable, String version,
                String image, int requirements, String icon, Map<String, String> configs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.create = create;
        this.enable = enable;
        this.version = version;
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

    public String getDescription() {
        return description;
    }

    public List<String> getDescriptionSplit() {
        return Arrays.asList(description.split("\\n"));
    }

    public Date getCreate() {
        return create;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getVersion() {
        return version;
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
