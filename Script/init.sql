CREATE DATABASE xyy_erp;

USE xyy_erp;

/**
 * 测试脚本
CREATE TABLE `tb_sys_user` (
  `id` bigint(16) NOT NULL auto_increment,
  `name` varchar(32) NULL,
  `sex` int(1) NULL,
  `age` int(1) NULL,
  `mobile` varchar(16) NULL,
  `address` varchar(128) NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `tb_sys_user` (`name`, `sex`, `age`, `mobile`, `address`) VALUES ('小明', 1, 20, '13582659846', '武汉市江岸区');
INSERT INTO `tb_sys_user` (`name`, `sex`, `age`, `mobile`, `address`) VALUES ('毛毛', 1, 18, '13698659999', '武汉市江汉区');
INSERT INTO `tb_sys_user` (`name`, `sex`, `age`, `mobile`, `address`) VALUES ('莉莉', 0, 24, '18588659555', '武汉市江夏区');
INSERT INTO `tb_sys_user` (`name`, `sex`, `age`, `mobile`, `address`) VALUES ('花花', 0, 21, '18698685686', '武汉市硚口区');
INSERT INTO `tb_sys_user` (`name`, `sex`, `age`, `mobile`, `address`) VALUES ('露露', 0, 26, '18288675586', '武汉市洪山区');
**/

/*********************
 *  系统管理相关脚本   *
 ********************/
/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017-01-11 12:58:44                          */
/*==============================================================*/


drop table if exists tb_sys_group;

drop table if exists tb_sys_group_role_relation;

drop table if exists tb_sys_group_user_relation;

drop table if exists tb_sys_module;

drop table if exists tb_sys_operation;

drop table if exists tb_sys_perm_res_relation;

drop table if exists tb_sys_permission;

drop table if exists tb_sys_resources;

drop table if exists tb_sys_role;

drop table if exists tb_sys_role_perm_relation;

drop table if exists tb_sys_role_user_relation;

drop table if exists tb_sys_user;

drop table if exists tb_app_menu;

/*==============================================================*/
/* Table: tb_sys_group                                          */
/*==============================================================*/
create table tb_sys_group
(
   id                   varchar(32) not null,
   parentId            varchar(32),
   groupName           varchar(32) not null,
   sortNo              int,
   state                int(1),
   createTime          datetime,
   creator              varchar(32),
   updateTime          datetime,
   remark               varchar(256),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_group_role_relation                            */
/*==============================================================*/
create table tb_sys_group_role_relation
(
   id                   varchar(32) not null,
   userId          varchar(32),
   roleId          varchar(32),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_group_user_relation                            */
/*==============================================================*/
create table tb_sys_group_user_relation
(
   id                   varchar(32) not null,
   groupId         varchar(32),
   userId          varchar(32),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_module                                         */
/*==============================================================*/
CREATE TABLE `tb_sys_module` (
  `id` varchar(32) NOT NULL,
  `parentId` varchar(32) DEFAULT NULL,
  `resName` varchar(16) DEFAULT NULL,
  `sortNo` int(11) DEFAULT NULL,
  `visiabled` int(1) DEFAULT NULL,
  `state` int(1) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `remark` varchar(256) DEFAULT NULL,
  `link` varchar(32) DEFAULT NULL,
  `icon` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;







/*==============================================================*/
/* Table: tb_sys_operation                                      */
/*==============================================================*/
CREATE TABLE `tb_sys_operation` (
  `id` varchar(32) NOT NULL,
  `moduleId` varchar(32) DEFAULT NULL,
  `sn` varchar(32) DEFAULT NULL,
  `icon` varchar(32) DEFAULT NULL,
  `tip` varchar(32) DEFAULT NULL,
  `showText` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/*==============================================================*/
/* Table: tb_sys_perm_res_relation                              */
/*==============================================================*/
create table tb_sys_perm_res_relation
(
   id                   varchar(32) not null,
   permId          varchar(32),
   resId           varchar(32),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_permission                                     */
/*==============================================================*/
create table tb_sys_permission
(
   id                   varchar(32) not null,
   parentId            varchar(32),
   permName            varchar(16),
   sortNo              int,
   state                int(1),
   permType            int(1),
   createTime          datetime,
   updator              varchar(32),
   updateTime          datetime,
   remark               varchar(256),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: tb_sys_role                                           */
/*==============================================================*/
create table tb_sys_role
(
   id                   varchar(32) not null,
   parentId            varchar(32),
   roleName            varchar(16),
   sortNo              int,
   state                int(1),
   inheritable          int(1),
   createTime          datetime,
   creator              varchar(32),
   updateTime          datetime,
   remark               varchar(256),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_role_perm_relation                             */
/*==============================================================*/
create table tb_sys_role_perm_relation
(
   id                   varchar(32) not null,
   roleId          varchar(32),
   permId          varchar(32),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_role_user_relation                             */
/*==============================================================*/
create table tb_sys_role_user_relation
(
   id                   varchar(32) not null,
   roleId          varchar(32),
   userId          varchar(32),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_sys_user                                           */
/*==============================================================*/
create table tb_sys_user
(
   id                   varchar(32) not null,
   loginName           varchar(16),
   password             varchar(32),
   realName            varchar(16),
   gender               int(1),
   mobile               varchar(16),
   sortNo              int,
   state                int(1),
   createTime          datetime,
   creator              varchar(32),
   updateTime          datetime,
   remark               varchar(256),
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_app_menu                                           */
/*==============================================================*/
CREATE TABLE `tb_app_menu` (
  `id` varchar(36) NOT NULL,
  `name` varchar(225) DEFAULT NULL COMMENT '名称',
  `userId` varchar(36) DEFAULT NULL COMMENT '用户id',
  `icon` varchar(225) DEFAULT NULL COMMENT '图标',
  `billType` varchar(225) DEFAULT NULL COMMENT '类型',
  `sortNo` int(11) DEFAULT NULL COMMENT '排序',
  `url` varchar(255) DEFAULT NULL COMMENT '地址',
  `menuId` varchar(255) DEFAULT NULL COMMENT '菜单id',
  `key` varchar(225) DEFAULT NULL COMMENT '单据key',
  `creatTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/***
alter table tb_sys_group_role_relation add constraint FK_Reference_13 foreign key (sys_user_id)
      references tb_sys_group (id) on delete restrict on update restrict;

alter table tb_sys_group_role_relation add constraint FK_Reference_14 foreign key (sys_role_id)
      references tb_sys_role (ID) on delete restrict on update restrict;

alter table tb_sys_group_user_relation add constraint FK_Reference_10 foreign key (sys_group_id)
      references tb_sys_group (id) on delete restrict on update restrict;

alter table tb_sys_group_user_relation add constraint FK_Reference_11 foreign key (sys_user_Id)
      references tb_sys_user (id) on delete restrict on update restrict;

alter table tb_sys_perm_res_relation add constraint FK_Reference_7 foreign key (sys_perm_id)
      references tb_sys_permission (id) on delete restrict on update restrict;

alter table tb_sys_perm_res_relation add constraint FK_Reference_8 foreign key (sys_res_id)
      references tb_sys_resources (ID) on delete restrict on update restrict;

alter table tb_sys_role_perm_relation add constraint FK_Reference_5 foreign key (sys_role_id)
      references tb_sys_role (ID) on delete restrict on update restrict;

alter table tb_sys_role_perm_relation add constraint FK_Reference_6 foreign key (sys_perm_id)
      references tb_sys_permission (id) on delete restrict on update restrict;

alter table tb_sys_role_user_relation add constraint FK_Reference_3 foreign key (sys_user_id)
      references tb_sys_user (id) on delete restrict on update restrict;

alter table tb_sys_role_user_relation add constraint FK_Reference_4 foreign key (sys_role_id)
      references tb_sys_role (ID) on delete restrict on update restrict;
**/