-- HostManager MySQL/MariaDB schema

START TRANSACTION;

-- TABLES setup

CREATE TABLE `{prefix}games` (
  `game_id` smallint(5) UNSIGNED NOT NULL,
  `game_name` varchar(32) NOT NULL COMMENT 'Game name',
  `create_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Date when this line was added, UTC time',
  `enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Bool: if the game is enable and playable',
  `game_version` varchar(20) NOT NULL COMMENT 'Game version',
  `server_version` varchar(20) NOT NULL COMMENT 'Minecraft server version',
  `image` varchar(255) NOT NULL COMMENT 'Full image repo of this game',
  `requirements` smallint(5) UNSIGNED NOT NULL DEFAULT 2048 COMMENT 'amount of needed RAM to run this game (MB)',
  `icon` varchar(64) NOT NULL DEFAULT 'GRASS' COMMENT 'Bukkit material id of item',
  `configs` LONGTEXT null comment 'VAR=VALUE;'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of all created games';
CREATE INDEX `{prefix}game_id` ON `{prefix}games` (`game_name`);

CREATE TABLE `{prefix}instances` (
  `instance_id` tinyint(3) UNSIGNED NOT NULL,
  `instance_name` varchar(64) NOT NULL COMMENT 'Instance name',
  `instance_server_id` varchar(36) DEFAULT NULL COMMENT 'Id of instance server',
  `instance_description` varchar(128) NOT NULL COMMENT 'Instance description',
  `instance_message` longtext NULL COMMENT 'Custom instance message',
  `port` smallint(5) NOT NULL COMMENT 'Instance port',
  `state` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Game state\n0: Creating\n1: Ready\n2: In progress\n3: Ending\n4: Terminated',
  `game` smallint(5) UNSIGNED NULL COMMENT 'Game of this instance',
  `user` int(10) UNSIGNED DEFAULT NULL COMMENT 'Host user',
  `creation` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Date of instance creation',
  `deletion` timestamp NULL DEFAULT current_timestamp() COMMENT 'Date of instance deletion'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='This table list all instances, with their current state';
CREATE INDEX `{prefix}instances_name` ON `{prefix}instances` (`instance_server_id`);

CREATE TABLE `{prefix}logs` (
  `log_id` int(10) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Log date',
  `instance` tinyint(3) UNSIGNED DEFAULT NULL COMMENT 'Id of targeted instance',
  `action` varchar(16) NOT NULL COMMENT  'FETCH_RESOURCES(0)\nGAME_CREATE(1)\nGAME_READY(2)\nGAME_START(3)\nGAME_END(4)\nINSTANCE_DELETE(5)',
  `user` int(10) UNSIGNED NOT NULL COMMENT 'Who trigger this log (Should be an UUID)',
  `game` smallint(5) UNSIGNED NOT NULL COMMENT 'Game of this log'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='This table log every actions from host management system';

CREATE TABLE `{prefix}users` (
  `user_id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) NOT NULL COMMENT 'UUID of player',
  `last_name` varchar(16) NOT NULL COMMENT 'Name of player',
  `tickets` smallint(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Amount of available host for this player'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of host users';
CREATE INDEX `{prefix}users_uuid` ON `{prefix}users` (`uuid`);

-- Index

ALTER TABLE `{prefix}games`
    ADD PRIMARY KEY (`game_id`),
    ADD UNIQUE KEY `unique_name` (`game_name`) USING BTREE;

ALTER TABLE `{prefix}instances`
    ADD PRIMARY KEY (`instance_id`),
    ADD KEY `user` (`user`),
    ADD KEY `game` (`game`);

ALTER TABLE `{prefix}logs`
    ADD PRIMARY KEY (`log_id`),
    ADD KEY `user` (`user`),
    ADD KEY `game` (`game`),
    ADD KEY `instance` (`instance`);

ALTER TABLE `{prefix}users`
    ADD PRIMARY KEY (`user_id`);

-- AUTO_INCREMENT

ALTER TABLE `{prefix}games`
    MODIFY `game_id` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}instances`
    MODIFY `instance_id` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}logs`
    MODIFY `log_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}users`
    MODIFY `user_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

-- FOREIGN KEY

ALTER TABLE `{prefix}instances`
    ADD CONSTRAINT `{prefix}instances_ibfk_1` FOREIGN KEY (`user`) REFERENCES `{prefix}users` (`user_id`),
    ADD CONSTRAINT `{prefix}instances_ibfk_2` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`game_id`);

ALTER TABLE `{prefix}logs`
    ADD CONSTRAINT `{prefix}logs_ibfk_1` FOREIGN KEY (`user`) REFERENCES `{prefix}users` (`user_id`),
    ADD CONSTRAINT `{prefix}logs_ibfk_2` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`game_id`),
    ADD CONSTRAINT `{prefix}logs_ibfk_3` FOREIGN KEY (`instance`) REFERENCES `{prefix}instances` (`instance_id`);

COMMIT;