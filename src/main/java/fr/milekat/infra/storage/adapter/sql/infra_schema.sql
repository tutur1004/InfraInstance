-- HostManager MySQL/MariaDB schema

START TRANSACTION;

-- TABLES setup

CREATE TABLE `{prefix}games` (
`id` smallint(5) UNSIGNED NULL,
`name` varchar(32) NOT NULL COMMENT 'Game name',
`description` varchar(256) NOT NULL COMMENT 'Game description',
`create_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Date when this line was added, UTC time',
`enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Bool: if the game is enable and playable',
`version` varchar(20) NOT NULL COMMENT 'Game version',
`image` varchar(255) NOT NULL COMMENT 'Full image repo of this game',
`requirements` smallint(5) UNSIGNED NOT NULL DEFAULT 2048 COMMENT 'amount of needed RAM to run this game (MB)',
`icon` varchar(64) NOT NULL DEFAULT 'GRASS' COMMENT 'Bukkit material id of item, or a base64 texture (Format "t:<b64>")'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of all created games';
CREATE INDEX `{prefix}games_id` ON `{prefix}games` (`name`);

CREATE TABLE `{prefix}instances` (
`id` tinyint(3) UNSIGNED NOT NULL,
`name` varchar(64) NOT NULL COMMENT 'Instance name',
`server_id` varchar(64) DEFAULT NULL COMMENT 'Id of instance server',
`description` varchar(256) NOT NULL COMMENT 'Instance description',
`message` longtext NULL COMMENT 'Custom instance message',
`hostname` VARCHAR(64) NOT NULL DEFAULT 'localhost' COMMENT 'Hostname of your instance',
`port` smallint(5) NOT NULL COMMENT 'Instance port',
`state` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Game state\n0: Creating\n1: Ready\n2: In progress\n3: Ending\n4: Terminated',
`access` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Access state\n0: PRIVATE\n1: REQUEST_TO_JOIN\n2: OPEN',
`game` smallint(5) UNSIGNED NULL COMMENT 'Game of this instance',
`user` int(10) UNSIGNED DEFAULT NULL COMMENT 'Host user',
`creation` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Date of instance creation',
`deletion` timestamp NULL DEFAULT current_timestamp() COMMENT 'Date of instance deletion'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='This table list all instances, with their current state';
CREATE INDEX `{prefix}instances_server_id` ON `{prefix}instances` (`server_id`);

CREATE TABLE `{prefix}logs` (
`id` int(10) UNSIGNED NOT NULL,
`date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Log date',
`instance` tinyint(3) UNSIGNED DEFAULT NULL COMMENT 'Id of targeted instance',
`action` varchar(16) NOT NULL COMMENT  'FETCH_RESOURCES(0)\nGAME_CREATE(1)\nGAME_READY(2)\nGAME_START(3)\nGAME_END(4)\nINSTANCE_DELETE(5)',
`user` int(10) UNSIGNED NOT NULL COMMENT 'Who trigger this log (Should be an UUID)',
`game` smallint(5) UNSIGNED NOT NULL COMMENT 'Game of this log'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='This table log every actions from infra management system';

CREATE TABLE `{prefix}users` (
`id` int(10) UNSIGNED NOT NULL,
`uuid` varchar(36) NOT NULL COMMENT 'UUID of player',
`last_name` varchar(16) NOT NULL COMMENT 'Name of player',
`tickets` smallint(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Amount of available host for this player'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of host users';
CREATE INDEX `{prefix}users_uuid` ON `{prefix}users` (`uuid`);

CREATE TABLE `{prefix}profiles` (
`id` int(10) UNSIGNED NOT NULL,
`name` varchar(32) NOT NULL COMMENT 'Game name',
`enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Bool: if this property is enabled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of all properties (Env Vars)';
CREATE INDEX `{prefix}profiles_id` ON `{prefix}profiles` (`id`);

CREATE TABLE `{prefix}properties` (
`id` int(10) UNSIGNED NOT NULL,
`name` varchar(32) NOT NULL COMMENT 'Game name',
`value` LONGTEXT null comment 'Value of this property',
`profile` int(10) UNSIGNED NOT NULL COMMENT 'Profile of this property',
`enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Bool: if this property is enabled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of all properties (Env Vars)';
CREATE INDEX `{prefix}properties_id` ON `{prefix}properties` (`id`);

CREATE TABLE `{prefix}game_strategies` (
`id` int(10) UNSIGNED NOT NULL,
`game` smallint(5) UNSIGNED NULL COMMENT 'Game id',
`profile` int(10) UNSIGNED NOT NULL COMMENT 'Profile id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Game profiles strategies';
CREATE INDEX `{prefix}games_strategies` ON `{prefix}game_strategies` (`game`);

-- Index

ALTER TABLE `{prefix}games`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `unique_name_version` UNIQUE (`name`, `version`);

ALTER TABLE `{prefix}instances`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `unique_name` UNIQUE (`name`),
    ADD CONSTRAINT `unique_server_id` UNIQUE (`server_id`),
    ADD KEY `user` (`user`),
    ADD KEY `game` (`game`);

ALTER TABLE `{prefix}logs`
    ADD PRIMARY KEY (`id`),
    ADD KEY `user` (`user`),
    ADD KEY `game` (`game`),
    ADD KEY `instance` (`instance`);

ALTER TABLE `{prefix}users`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `unique_uuid` UNIQUE (`uuid`);

ALTER TABLE `{prefix}profiles`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `unique_name` UNIQUE (`name`);

ALTER TABLE `{prefix}properties`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `unique_name_profile` UNIQUE (`name`, `profile`);

ALTER TABLE `{prefix}game_strategies`
    ADD PRIMARY KEY (id),
    ADD CONSTRAINT `unique_strategy` UNIQUE (`game`,`profile`);

-- AUTO_INCREMENT

ALTER TABLE `{prefix}games`
    MODIFY `id` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}instances`
    MODIFY `id` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}logs`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}users`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}profiles`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}properties`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}game_strategies`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

-- FOREIGN KEY

ALTER TABLE `{prefix}instances`
    ADD CONSTRAINT `{prefix}instances_fk_1` FOREIGN KEY (`user`) REFERENCES `{prefix}users` (`id`),
    ADD CONSTRAINT `{prefix}instances_fk_2` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`id`);

ALTER TABLE `{prefix}logs`
    ADD CONSTRAINT `{prefix}logs_fk_1` FOREIGN KEY (`user`) REFERENCES `{prefix}users` (`id`),
    ADD CONSTRAINT `{prefix}logs_fk_2` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`id`),
    ADD CONSTRAINT `{prefix}logs_fk_3` FOREIGN KEY (`instance`) REFERENCES `{prefix}instances` (`id`);

ALTER TABLE `{prefix}properties`
    ADD CONSTRAINT `{prefix}properties_fk_1` FOREIGN KEY (`profile`) REFERENCES `{prefix}profiles` (`id`);

ALTER TABLE `{prefix}game_strategies`
    ADD CONSTRAINT `{prefix}strategies_fk_1` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`id`),
    ADD CONSTRAINT `{prefix}strategies_fk_2` FOREIGN KEY (`profile`) REFERENCES `{prefix}profiles` (`id`);

-- INSERT global profile

INSERT INTO `{prefix}profiles` (`id`, `name`, `enable`)
VALUES (1, 'global', true)
ON DUPLICATE KEY UPDATE `name`='global', `enable`=true;

COMMIT;