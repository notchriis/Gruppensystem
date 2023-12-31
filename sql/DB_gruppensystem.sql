-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 31 déc. 2023 à 16:50
-- Version du serveur : 10.4.28-MariaDB
-- Version de PHP : 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `gruppensystem`
--

-- --------------------------------------------------------

--
-- Structure de la table `gruppe`
--

CREATE TABLE `gruppe` (
  `gruppe_id` int(11) NOT NULL,
  `name` varchar(80) DEFAULT NULL,
  `prefix` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `gruppe`
--

INSERT INTO `gruppe` (`gruppe_id`, `name`, `prefix`) VALUES
(1, 'Besucher', 'Bes'),
(2, 'Admin', 'OP'),
(3, 'VIP', 'vip');

-- --------------------------------------------------------

--
-- Structure de la table `spieler`
--

CREATE TABLE `spieler` (
  `spieler_id` varchar(36) NOT NULL,
  `ingame_name` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `spieler`
--

INSERT INTO `spieler` (`spieler_id`, `ingame_name`) VALUES
('Test', 'Test!');

-- --------------------------------------------------------

--
-- Structure de la table `spielergruppe`
--

CREATE TABLE `spielergruppe` (
  `spieler_id` varchar(36) NOT NULL,
  `gruppe_id` int(11) NOT NULL,
  `beitrittsdatum` datetime NOT NULL,
  `austrittsdatum` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `spielergruppe`
--

INSERT INTO `spielergruppe` (`spieler_id`, `gruppe_id`, `beitrittsdatum`, `austrittsdatum`) VALUES
('Test', 1, '2023-12-23 00:16:59', NULL),
('Test', 2, '2023-12-23 00:17:08', NULL);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `gruppe`
--
ALTER TABLE `gruppe`
  ADD PRIMARY KEY (`gruppe_id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Index pour la table `spieler`
--
ALTER TABLE `spieler`
  ADD PRIMARY KEY (`spieler_id`),
  ADD UNIQUE KEY `ingame_name` (`ingame_name`);

--
-- Index pour la table `spielergruppe`
--
ALTER TABLE `spielergruppe`
  ADD PRIMARY KEY (`spieler_id`,`gruppe_id`),
  ADD KEY `gruppe_id` (`gruppe_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `gruppe`
--
ALTER TABLE `gruppe`
  MODIFY `gruppe_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `spielergruppe`
--
ALTER TABLE `spielergruppe`
  ADD CONSTRAINT `spielergruppe_ibfk_1` FOREIGN KEY (`spieler_id`) REFERENCES `spieler` (`spieler_id`),
  ADD CONSTRAINT `spielergruppe_ibfk_2` FOREIGN KEY (`gruppe_id`) REFERENCES `gruppe` (`gruppe_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
