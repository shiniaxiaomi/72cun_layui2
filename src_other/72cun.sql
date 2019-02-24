/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.0.27-community-nt : Database - 72cun
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
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `pid` int(11) default NULL,
  `userId` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_userId_pid` (`userId`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `hoturl` */

DROP TABLE IF EXISTS `hoturl`;

CREATE TABLE `hoturl` (
  `id` int(11) NOT NULL auto_increment,
  `urlId` int(11) default NULL COMMENT '链接的id',
  `clickNumber` int(11) default NULL COMMENT '点击量',
  `goodNumber` int(11) default NULL COMMENT '点赞量',
  PRIMARY KEY  (`id`),
  KEY `index_urlId` (`urlId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `id` int(11) NOT NULL auto_increment,
  `detail` varchar(200) default NULL,
  `userId` int(11) default NULL COMMENT '发布消息的用户id',
  `sendTime` datetime default NULL COMMENT '发送时间',
  `rootId` int(11) default '0' COMMENT '根节点id',
  `isMark` tinyint(1) default '0' COMMENT '标记在消息状态',
  `userName` varchar(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_rootId_userId` (`rootId`,`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `url` */

DROP TABLE IF EXISTS `url`;

CREATE TABLE `url` (
  `id` int(11) NOT NULL auto_increment,
  `url` varchar(600) default NULL,
  `label` varchar(100) default NULL,
  `pid` int(11) default NULL,
  `userId` int(11) default NULL,
  `createTime` datetime default NULL,
  `pidName` varchar(20) default NULL,
  `isShare` tinyint(1) default NULL COMMENT '标记是否共享',
  `shareTime` datetime default NULL COMMENT '链接共享时间',
  PRIMARY KEY  (`id`),
  KEY `index_userId_pid` (`userId`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `password` varchar(100) default NULL,
  `userName` varchar(100) default NULL,
  `rootFolderId` int(11) default NULL,
  `customFolderId` int(11) default '0',
  `customFolderName` varchar(40) default NULL,
  `lastLoginTime` datetime default NULL,
  `phoneNumber` char(11) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `UK_hl8fftx66p59oqgkkcfit3eay` (`userName`),
  UNIQUE KEY `index_phoneNumber` (`phoneNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `user_hoturl` */

DROP TABLE IF EXISTS `user_hoturl`;

CREATE TABLE `user_hoturl` (
  `userId` int(11) default NULL COMMENT '用户id',
  `likeUrlId` int(11) default NULL COMMENT '点赞的urlId',
  KEY `index_userId_likeUrlId` (`userId`,`likeUrlId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
