CREATE TABLE `int_wms_bsbssc_bill` (
  `djbh` varchar(255) DEFAULT NULL,
  `rq` date DEFAULT NULL COMMENT '日期',
  `bmid` varchar(255) DEFAULT NULL,
  `ywy` varchar(255) DEFAULT NULL,
  `dj_sort` varchar(255) DEFAULT NULL,
  `spid` varchar(255) DEFAULT NULL COMMENT '商品id',
  `pyshl` decimal(14,6) DEFAULT '0.000000' COMMENT '盘溢数量',
  `psshl` decimal(14,6) DEFAULT '0.000000' COMMENT '盘损数量',
  `ph` varchar(255) DEFAULT NULL COMMENT '批号',
  `yxqz` date DEFAULT NULL COMMENT '有效期至',
  `rq_sc` date DEFAULT NULL COMMENT '生产日期',
  `kczt` int(11) DEFAULT '0' COMMENT '库存状态',
  `kfbh` varchar(255) DEFAULT NULL,
  `lastmodifytime` date DEFAULT NULL COMMENT '最后修改时间',
  `orgId` varchar(255) DEFAULT NULL COMMENT '机构ID',
  `orgCode` varchar(255) DEFAULT NULL COMMENT '机构编码',
  `is_zx` int(11) DEFAULT '0' COMMENT '是否迁移'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;