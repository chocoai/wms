CREATE TABLE `int_wms_cgrk_bill` (
  `orgId` varchar(50) DEFAULT NULL COMMENT '机构id',
  `orgCode` varchar(50) DEFAULT NULL COMMENT '机构编码',
  `kufang` varchar(50) DEFAULT NULL COMMENT '库房',
  `djbh` varchar(50) DEFAULT NULL COMMENT '单据编号',
  `dwbh` varchar(255) DEFAULT NULL COMMENT '单位内码',
  `rq` date DEFAULT NULL COMMENT '日期',
  `cgy` varchar(255) DEFAULT NULL COMMENT '采购员',
  `shy` varchar(255) DEFAULT NULL COMMENT '收货员',
  `zjy` varchar(255) DEFAULT NULL COMMENT '质检员',
  `czy` varchar(255) DEFAULT NULL COMMENT '操作员',
  `djbh_sj` varchar(255) DEFAULT NULL COMMENT '原采购订单编号',
  `rktype` int(11) DEFAULT '0' COMMENT '入库类型',
  `yzid` varchar(255) DEFAULT NULL COMMENT '业主内码',
  `dj_sort` int(11) DEFAULT '0' COMMENT '单据行号',
  `spid` varchar(255) DEFAULT NULL COMMENT '商品内码',
  `sl` decimal(14,2) DEFAULT '0.00' COMMENT '数量',
  `js` decimal(14,2) DEFAULT '0.00' COMMENT '件数',
  `lss` decimal(14,2) DEFAULT '0.00' COMMENT '零散数',
  `ph` varchar(255) DEFAULT NULL COMMENT '批号 ',
  `rq_sc` date DEFAULT NULL COMMENT '生产日期',
  `yxqz` date DEFAULT NULL COMMENT '有效期至',
  `cgdd_sort` int(11) DEFAULT '0' COMMENT '采购订单行号',
  `yspd` int(11) DEFAULT '0' COMMENT '验收评定',
  `lastmodifytime` date DEFAULT NULL COMMENT '上传单据时间',
  `is_zx` int(11) DEFAULT '0' COMMENT '状态回写',
  `recnum` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `int_wms_xsth_bill` (
  `orgId` varchar(50) DEFAULT NULL COMMENT '机构id',
  `orgCode` varchar(50) DEFAULT NULL COMMENT '机构编码',
  `kufang` varchar(50) DEFAULT NULL COMMENT '库房',
  `djbh` varchar(50) DEFAULT NULL COMMENT '单据编号',
  `dwbh` varchar(255) DEFAULT NULL COMMENT '单位内码',
  `rq` date DEFAULT NULL COMMENT '日期',
  `ywy` varchar(255) DEFAULT NULL COMMENT '业务员',
  `zjy` varchar(255) DEFAULT NULL COMMENT '质检员',
  `thlb` varchar(255) DEFAULT NULL COMMENT '退货类别',
  `yzid` varchar(255) DEFAULT NULL COMMENT '业主内码',
  `dj_sort` varchar(255) DEFAULT NULL COMMENT '单据行号',
  `spid` varchar(255) DEFAULT NULL COMMENT '商品内码',
  `sl` decimal(14,2) DEFAULT '0.00' COMMENT '数量',
  `js` decimal(14,2) DEFAULT '0.00' COMMENT '件数',
  `lss` decimal(14,2) DEFAULT '0.00' COMMENT '零散数',
  `ph` varchar(255) DEFAULT NULL COMMENT '批号 ',
  `rq_sc` date DEFAULT NULL COMMENT '生产日期',
  `yxqz` date DEFAULT NULL COMMENT '有效期至',
  `yspd` int(11) DEFAULT '0' COMMENT '验收评定',
  `lastmodifytime` date DEFAULT NULL COMMENT '上传单据时间',
  `is_zx` int(11) DEFAULT '0' COMMENT '状态回写',
  `bmid` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `int_wms_gjtc_bill` (
  `orgId` varchar(50) DEFAULT NULL COMMENT '机构id',
  `orgCode` varchar(50) DEFAULT NULL COMMENT '机构编码',
  `kufang` varchar(50) DEFAULT NULL COMMENT '库房',
  `djbh` varchar(50) DEFAULT NULL COMMENT '单据编号',
  `dwbh` varchar(255) DEFAULT NULL COMMENT '单位内码',
  `rq` date DEFAULT NULL COMMENT '日期',
  `thy` varchar(255) DEFAULT NULL COMMENT '退货员',
  `cgy` varchar(255) DEFAULT NULL COMMENT '采购员',
  `yzid` varchar(255) DEFAULT NULL COMMENT '业主内码',
  `dj_sort` varchar(255) DEFAULT NULL COMMENT '单据行号',
  `spid` varchar(255) DEFAULT NULL COMMENT '商品内码',
  `sl` decimal(14,2) DEFAULT '0.00' COMMENT '数量',
  `js` decimal(14,2) DEFAULT '0.00' COMMENT '件数',
  `lss` decimal(14,2) DEFAULT '0.00' COMMENT '零散数',
  `ph` varchar(255) DEFAULT NULL COMMENT '批号 ',
  `rq_sc` date DEFAULT NULL COMMENT '生产日期',
  `yxqz` date DEFAULT NULL COMMENT '有效期至',
  `kczt` varchar(255) DEFAULT NULL COMMENT '库存状态',
  `lastmodifytime` date DEFAULT NULL COMMENT '上传单据时间',
  `is_zx` int(11) DEFAULT '0' COMMENT '状态',
  `billsn` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `v_erp_cgdd`;
CREATE TABLE `v_erp_cgdd` (
  `djbh` varchar(20) NOT NULL DEFAULT '',
  `dwid` varchar(20) DEFAULT '',
  `rq` varchar(10) DEFAULT '',
  `RY_cgy` varchar(20) DEFAULT '',
  `bmid` varchar(20) DEFAULT '',
  `sf_zy` varchar(2) DEFAULT '',
  `rktype` varchar(20) DEFAULT '',
  `yzid` varchar(11) NOT NULL DEFAULT '',
  `dj_sort` int(11) NOT NULL DEFAULT '0',
  `spid` varchar(20) DEFAULT '',
  `sl` decimal(14,2) DEFAULT '0.00',
  `js` decimal(14,2) DEFAULT '0.00',
  `lss` decimal(14,2) DEFAULT '0.00',
  `ph` varchar(20) DEFAULT '',
  `rq_sc` varchar(10) DEFAULT '',
  `yxqz` varchar(10) DEFAULT '',
  `wmflg` varchar(2) DEFAULT 'N',
  `lastmodifytime` varchar(19) DEFAULT '',
  `fhdz` varchar(120) DEFAULT NULL,
  `dj` decimal(18,4) DEFAULT '0.0000',
  `je` decimal(18,2) DEFAULT '0.00',
  `orgId` varchar(50) DEFAULT '',
  `orgCode` varchar(50) DEFAULT '',
  `kufang` varchar(50) DEFAULT '',
  PRIMARY KEY (`djbh`,`yzid`,`dj_sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of v_erp_cgdd
-- ----------------------------

-- ----------------------------
-- Table structure for `v_erp_gjtc`
-- ----------------------------
DROP TABLE IF EXISTS `v_erp_gjtc`;
CREATE TABLE `v_erp_gjtc` (
  `djbh` varchar(20) NOT NULL DEFAULT '',
  `dwbh` varchar(20) DEFAULT '',
  `rq` varchar(10) DEFAULT '',
  `ywy` varchar(20) DEFAULT '',
  `cgy` varchar(20) DEFAULT '',
  `bmid` varchar(20) DEFAULT '',
  `beizhu` varchar(60) DEFAULT '',
  `thlb` varchar(20) DEFAULT '',
  `yzid` varchar(11) NOT NULL DEFAULT '',
  `dj_sort` int(11) NOT NULL DEFAULT '0',
  `spid` varchar(20) DEFAULT '',
  `sl` decimal(14,2) DEFAULT '0.00',
  `js` decimal(14,2) DEFAULT '0.00',
  `lss` decimal(14,2) DEFAULT '0.00',
  `thyy` varchar(40) DEFAULT '',
  `ph` varchar(20) DEFAULT '',
  `wmflg` varchar(2) DEFAULT 'N',
  `lastmodifytime` varchar(19) DEFAULT '',
  `orgId` varchar(50) DEFAULT '',
  `orgCode` varchar(50) DEFAULT '',
  `kufang` varchar(50) DEFAULT '',
  PRIMARY KEY (`djbh`,`yzid`,`dj_sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of v_erp_gjtc
-- ----------------------------

-- ----------------------------
-- Table structure for `v_erp_xsth`
-- ----------------------------
DROP TABLE IF EXISTS `v_erp_xsth`;
CREATE TABLE `v_erp_xsth` (
  `djbh` varchar(20) NOT NULL DEFAULT '',
  `dwbh` varchar(20) DEFAULT '',
  `rq` varchar(10) DEFAULT '',
  `bmid` varchar(20) DEFAULT '',
  `ry_ywy` varchar(20) DEFAULT '',
  `ry_zjy` varchar(20) DEFAULT '',
  `thlb` varchar(20) DEFAULT '',
  `yzid` varchar(11) NOT NULL DEFAULT '',
  `dj_sort` int(11) NOT NULL DEFAULT '0',
  `spid` varchar(20) DEFAULT '',
  `sl` decimal(14,2) DEFAULT '0.00',
  `js` decimal(14,2) DEFAULT '0.00',
  `lss` decimal(14,2) DEFAULT '0.00',
  `thyy` varchar(40) DEFAULT '',
  `ph` varchar(20) DEFAULT '',
  `rq_sc` varchar(20) DEFAULT '',
  `yxqz` varchar(20) DEFAULT '',
  `yspd` varchar(10) DEFAULT '',
  `mjph` varchar(20) DEFAULT '',
  `jkzczh` varchar(50) DEFAULT '',
  `wmflg` varchar(2) DEFAULT 'N',
  `lastmodifytime` varchar(19) DEFAULT '',
  `orgId` varchar(50) DEFAULT '',
  `orgCode` varchar(50) DEFAULT '',
  `kufang` varchar(50) DEFAULT '',
  PRIMARY KEY (`djbh`,`yzid`,`dj_sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

