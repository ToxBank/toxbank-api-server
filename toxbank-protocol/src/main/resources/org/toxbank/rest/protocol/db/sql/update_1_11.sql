ALTER TABLE `protocol` CHANGE COLUMN `status` `status` ENUM('RESEARCH','SOP','REPORT') NOT NULL DEFAULT 'RESEARCH' COMMENT 'Research or Standard Operating Procedure'  ;
insert into version (idmajor,idminor,comment) values (1,11,"TB Protocol schema");
