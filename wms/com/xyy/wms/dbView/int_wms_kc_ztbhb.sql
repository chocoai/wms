CREATE TABLE `int_wms_kc_ztbhb` (
  `sp_sort` varbinary(36) DEFAULT NULL,
  `spid` varchar(36) NOT NULL COMMENT '商品id',
  `kczt_old` int(11) DEFAULT NULL COMMENT '移库之前库存状态',
  `kczt_new` int(11) DEFAULT NULL COMMENT '移库之后库存状态',
  `kfbh_old` int(11) DEFAULT NULL,
  `kfbh_new` int(11) DEFAULT NULL,
  `ph` varchar(36) DEFAULT NULL COMMENT '批号',
  `rq_sc` date DEFAULT NULL COMMENT '生产日期',
  `yxqz` date DEFAULT NULL COMMENT '有效期至',
  `change_time` date DEFAULT NULL COMMENT '移库时间',
  `lastmodifytime` date DEFAULT NULL COMMENT '最后更新时间',
  `sl_kc` decimal(10,0) DEFAULT NULL COMMENT '移库数量',
  `is_zx` int(11) DEFAULT NULL COMMENT '是否迁移',
  `yzid` varchar(255) NOT NULL,
  `orgId` varchar(255) DEFAULT NULL COMMENT '组织机构id',
  `orgCode` varchar(255) DEFAULT NULL COMMENT '组织机构编码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
