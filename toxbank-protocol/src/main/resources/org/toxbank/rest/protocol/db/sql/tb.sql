-- CREATE DATABASE `tb` /*!40100 DEFAULT CHARACTER SET utf8 */;

-- Protocols metadata

DROP TABLE IF EXISTS ``protocol`;
CREATE TABLE  `protocol` (
  `idprotocol` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `identifier` varchar(45) DEFAULT NULL COMMENT 'Unique human readable ID',
  `title` varchar(45) NOT NULL COMMENT 'Title',
  `abstract` text,
  `author` varchar(45) NOT NULL COMMENT 'Username@domain',
  `summarySearchable` tinyint(1) NOT NULL DEFAULT '1',
  `project` varchar(45) NOT NULL COMMENT 'Project name',
  `filename` text COMMENT 'Path to the file',
  PRIMARY KEY (`idprotocol`),
  UNIQUE KEY `Index_2` (`identifier`),
  KEY `Index_3` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Keywords. Want to do full text search, thus MyISAM. Could be changed eventually.

DROP TABLE IF EXISTS `keyword`;
CREATE TABLE  `keyword` (
  `idprotocol` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `words` text NOT NULL,
  PRIMARY KEY (`idprotocol`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;