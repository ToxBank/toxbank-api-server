-- CREATE DATABASE `tb` /*!40100 DEFAULT CHARACTER SET utf8 */;

-- Users. If registered, have OpenAM user name

DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL COMMENT 'OpenAM user name',
  `title` varchar(45) DEFAULT NULL,
  `firstname` varchar(45) NOT NULL,
  `lastname` varchar(45) NOT NULL,
  `institute` varchar(45) DEFAULT NULL,
  `weblog` varchar(45) DEFAULT NULL,
  `homepage` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`iduser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Organisation, project, linked to ldap groups

DROP TABLE IF EXISTS `organisation`;
CREATE TABLE  `organisation` (
  `idorganisation` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) NOT NULL,
  PRIMARY KEY (`idorganisation`),
  UNIQUE KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `project`;
CREATE TABLE  `project` (
  `idproject` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) NOT NULL,
  PRIMARY KEY (`idproject`),
  UNIQUE KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Protocols metadata & placeholder for data templates. Versioning to be added.
DROP TABLE IF EXISTS `protocol`;
CREATE TABLE  `protocol` (
  `idprotocol` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Version',
  `identifier` varchar(45) DEFAULT NULL COMMENT 'Unique human readable ID',
  `title` varchar(45) NOT NULL COMMENT 'Title',
  `abstract` text,
  `summarySearchable` tinyint(1) NOT NULL DEFAULT '1',
  `iduser` int(10) unsigned NOT NULL COMMENT 'Link to user table',
  `idproject` int(10) unsigned NOT NULL COMMENT 'Link to projects table',
  `idorganisation` int(10) unsigned NOT NULL COMMENT 'Link to org table',
  `filename` text COMMENT 'Path to file name',
  `template` blob COMMENT 'Data template',
  PRIMARY KEY (`idprotocol`),
  UNIQUE KEY `Index_2` (`identifier`),
  KEY `Index_3` (`title`),
  KEY `FK_protocol_1` (`idproject`),
  KEY `FK_protocol_2` (`idorganisation`),
  KEY `FK_protocol_3` (`iduser`),
  CONSTRAINT `FK_protocol_3` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`),
  CONSTRAINT `FK_protocol_1` FOREIGN KEY (`idproject`) REFERENCES `project` (`idproject`),
  CONSTRAINT `FK_protocol_2` FOREIGN KEY (`idorganisation`) REFERENCES `organisation` (`idorganisation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Keywords. Want to do full text search, thus MyISAM. Could be changed eventually.

DROP TABLE IF EXISTS `keyword`;
CREATE TABLE  `keyword` (
  `idprotocol` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `words` text NOT NULL,
  PRIMARY KEY (`idprotocol`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


-- db version

DROP TABLE IF EXISTS `version`;
CREATE TABLE  `version` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
insert into version (idmajor,idminor,comment) values (0,4,"TB Protocol schema");