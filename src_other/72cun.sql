/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.6.40 : Database - 72cun
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`72cun` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `72cun`;

/*Table structure for table `folder` */

DROP TABLE IF EXISTS `folder`;

CREATE TABLE `folder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_userId_pid` (`userId`,`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `detail` varchar(200) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL COMMENT '发布消息的用户id',
  `sendTime` datetime DEFAULT NULL COMMENT '发送时间',
  `rootId` int(11) DEFAULT '0' COMMENT '根节点id',
  `isMark` tinyint(1) DEFAULT '0' COMMENT '标记在消息状态',
  `userName` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_rootId_userId` (`rootId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

/*Table structure for table `url` */

DROP TABLE IF EXISTS `url`;

CREATE TABLE `url` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(600) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `pidName` varchar(20) DEFAULT NULL,
  `isShare` tinyint(1) DEFAULT NULL COMMENT '标记是否共享',
  PRIMARY KEY (`id`),
  KEY `index_userId_pid` (`userId`,`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(100) DEFAULT NULL,
  `userName` varchar(100) DEFAULT NULL,
  `rootFolderId` int(11) DEFAULT NULL,
  `customFolderId` int(11) DEFAULT '0',
  `customFolderName` varchar(40) DEFAULT NULL,
  `lastLoginTime` datetime DEFAULT NULL,
  `phoneNumber` char(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hl8fftx66p59oqgkkcfit3eay` (`userName`),
  UNIQUE KEY `index_phoneNumber` (`phoneNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
