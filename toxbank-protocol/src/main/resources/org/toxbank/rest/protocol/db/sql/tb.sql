-- CREATE DATABASE `tb` /*!40100 DEFAULT CHARACTER SET utf8 */;

-- -----------------------------------------------------
-- Users. If registered, 'username' points to OpenAM user name
-- -----------------------------------------------------
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
  PRIMARY KEY (`iduser`),
  UNIQUE KEY `Index_2` (`username`),
  KEY `Index_3` (`lastname`,`firstname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Organisation, project, linked to OpenAM groups
-- -----------------------------------------------------
DROP TABLE IF EXISTS `organisation`;
CREATE TABLE  `organisation` (
  `idorganisation` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idorganisation`),
  UNIQUE KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Projects
-- -----------------------------------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE  `project` (
  `idproject` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idproject`),
  UNIQUE KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- -----------------------------------------------------
-- Protocols metadata & placeholder for data templates. 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `protocol`;
CREATE TABLE  `protocol` (
  `idprotocol` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Version',
  `title` varchar(45) NOT NULL COMMENT 'Title',
  `abstract` text,
  `summarySearchable` tinyint(1) NOT NULL DEFAULT '1',
  `iduser` int(10) unsigned NOT NULL COMMENT 'Link to user table',
  `idproject` int(10) unsigned NOT NULL COMMENT 'Link to projects table',
  `idorganisation` int(10) unsigned NOT NULL COMMENT 'Link to org table',
  `filename` text COMMENT 'Path to file name',
  `template` blob COMMENT 'Data template',
  `status` enum('RESEARCH','SOP') NOT NULL DEFAULT 'RESEARCH' COMMENT 'Research or Standard Operating Procedure',
  `latest` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Is the latest version',
  PRIMARY KEY (`idprotocol`,`version`) USING BTREE,
  KEY `Index_3` (`title`),
  KEY `FK_protocol_1` (`idproject`),
  KEY `FK_protocol_2` (`idorganisation`),
  KEY `FK_protocol_3` (`iduser`),
  KEY `Index_7` (`latest`),
  CONSTRAINT `FK_protocol_1` FOREIGN KEY (`idproject`) REFERENCES `project` (`idproject`),
  CONSTRAINT `FK_protocol_2` FOREIGN KEY (`idorganisation`) REFERENCES `organisation` (`idorganisation`),
  CONSTRAINT `FK_protocol_3` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Protocol authors
-- -----------------------------------------------------
DROP TABLE IF EXISTS `protocol_authors`;
CREATE TABLE  `protocol_authors` (
  `idprotocol` int(10) unsigned NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `iduser` int(10) unsigned NOT NULL,
  PRIMARY KEY (`idprotocol`,`version`,`iduser`) USING BTREE,
  KEY `FK_protocol_authors_2` (`iduser`),
  CONSTRAINT `FK_protocol_authors_1` FOREIGN KEY (`idprotocol`, `version`) REFERENCES `protocol` (`idprotocol`, `version`),
  CONSTRAINT `FK_protocol_authors_2` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Trigger 
-- -----------------------------------------------------
-- DELIMITER $
-- CREATE TRIGGER insert_protocol_id BEFORE UPDATE ON protocol
--FOR EACH ROW BEGIN
--	IF NEW.idprotocol != null THEN
--		set NEW.version = OLD.version+1;
--	END IF;
-- END $
-- DELIMITER ;

-- -----------------------------------------------------
-- Keywords. Want to do full text search, thus MyISAM. 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `keywords`;
CREATE TABLE  `keywords` (
  `idprotocol` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `keywords` text NOT NULL COMMENT 'All keywords semicolon delimited',
  PRIMARY KEY (`idprotocol`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- DB schema version
-- -----------------------------------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE  `version` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
insert into version (idmajor,idminor,comment) values (0,8,"TB Protocol schema");