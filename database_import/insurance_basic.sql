-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Počítač: 127.0.0.1
-- Vytvořeno: Úte 21. úno 2023, 04:59
-- Verze serveru: 10.4.27-MariaDB
-- Verze PHP: 8.1.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáze: `insurance_basic`
--

-- --------------------------------------------------------

--
-- Struktura tabulky `insurances`
--

CREATE TABLE `insurances` (
  `insurance_id` int(11) NOT NULL,
  `name` varchar(60) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `subject` varchar(60) DEFAULT NULL,
  `valid_from` datetime DEFAULT NULL,
  `valid_until` datetime DEFAULT NULL,
  `ip_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Vypisuji data pro tabulku `insurances`
--

INSERT INTO `insurances` (`insurance_id`, `name`, `amount`, `subject`, `valid_from`, `valid_until`, `ip_id`) VALUES
(1, 'Pojištění majetku', 10000000, 'Byt', '2022-01-01 00:00:00', '2022-12-31 00:00:00', 2),
(2, 'Úrazové pojištění', 70000, 'Petr Březina', '2023-02-15 00:00:00', '2024-02-14 00:00:00', 2),
(3, 'Pojištění odpovědnosti', 1000000, 'Adéla Rozínová', '2023-02-15 00:00:00', '2024-02-14 00:00:00', 3),
(4, 'Pojištění majetku', 12500000, 'Rodinný dům', '2023-03-01 00:00:00', '2024-02-28 00:00:00', 1),
(8, 'Úrazové pojištění', 500000, 'Jan Novák', '2023-03-01 00:00:00', '2024-02-29 00:00:00', 1),
(19, 'Pojištění domácnosti', 5000000, 'Byt', '2022-02-01 00:00:00', '2025-01-31 00:00:00', 3),
(20, 'Cestovní pojištění', 10000, 'Martin Holý', '2023-02-07 00:00:00', '2023-02-12 00:00:00', 12),
(21, 'Úrazové pojištění', 50000, 'Martin Holý', '2023-02-22 00:00:00', '2023-03-12 00:00:00', 12),
(22, 'Životní pojištění', 1000000, 'Petra Jedličková', '2022-12-01 00:00:00', '2024-11-30 00:00:00', 13),
(43, 'Životní pojištění', 1500000, 'Jana Hejdová', '2020-01-01 00:00:00', '2029-12-31 00:00:00', 31);

-- --------------------------------------------------------

--
-- Struktura tabulky `insured_persons`
--

CREATE TABLE `insured_persons` (
  `ip_id` int(11) NOT NULL,
  `first_name` varchar(60) DEFAULT NULL,
  `last_name` varchar(60) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `phone` varchar(60) DEFAULT NULL,
  `street` varchar(60) DEFAULT NULL,
  `city` varchar(60) DEFAULT NULL,
  `postcode` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Vypisuji data pro tabulku `insured_persons`
--

INSERT INTO `insured_persons` (`ip_id`, `first_name`, `last_name`, `email`, `phone`, `street`, `city`, `postcode`) VALUES
(1, 'Jan', 'Novák', 'jannovak88@seznam.cz', '731879632', 'Ve Svahu 3', 'Praha 1', '10000'),
(2, 'Petr', 'Březina', 'petr.brezina@gmail.com', '606500789', 'Palackého 4', 'Brno', '20000'),
(3, 'Adéla', 'Rozínová', 'roza@gmail.com', '606603472', 'Arabská 7', 'Praha 6', '16000'),
(4, 'Aleš', 'Hora', 'hora@seznam.cz', '603579546', 'Čkalova 24', 'Praha 1', '10000'),
(12, 'Martin', 'Holý', 'matin.holy@gmail.com', '604234797', 'U Nádraží 15', 'Brno', '70800'),
(13, 'Adam', 'Jedlička', 'adamama@seznam.cz', '606573298', 'U Kostela 32', 'Příbram', '24789'),
(14, 'Karel', 'Výborný', 'skvely@gmail.com', '602798304', 'Přípotoční 42', 'Praha 10', '13000'),
(31, 'Jaroslav', 'Hejda', 'jarohej@email.cz', '775570079', 'K Letišti', 'Praha 6', '16000');

--
-- Triggery `insured_persons`
--
DELIMITER $$
CREATE TRIGGER `after_delete_insured_person` AFTER DELETE ON `insured_persons` FOR EACH ROW BEGIN
	DELETE FROM insurances WHERE insurances.ip_id = OLD.ip_id;
END
$$
DELIMITER ;

--
-- Indexy pro exportované tabulky
--

--
-- Indexy pro tabulku `insurances`
--
ALTER TABLE `insurances`
  ADD PRIMARY KEY (`insurance_id`);

--
-- Indexy pro tabulku `insured_persons`
--
ALTER TABLE `insured_persons`
  ADD PRIMARY KEY (`ip_id`);

--
-- AUTO_INCREMENT pro tabulky
--

--
-- AUTO_INCREMENT pro tabulku `insurances`
--
ALTER TABLE `insurances`
  MODIFY `insurance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT pro tabulku `insured_persons`
--
ALTER TABLE `insured_persons`
  MODIFY `ip_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
