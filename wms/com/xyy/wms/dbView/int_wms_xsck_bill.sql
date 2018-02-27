/*
Navicat MySQL Data Transfer

Source Server         : xyy_erp
Source Server Version : 50721
Source Host           : 172.16.96.25:3306
Source Database       : xyy_wms_new

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-01-24 16:03:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for int_wms_xsck_bill
-- ----------------------------
DROP TABLE IF EXISTS `int_wms_xsck_bill`;
CREATE TABLE `int_wms_xsck_bill` (
  `DJBH` varchar(255) DEFAULT NULL COMMENT '单据编号（销售订单编号）',
  `DWBH` varchar(255) DEFAULT NULL COMMENT '单位内码',
  `RQ` date DEFAULT NULL COMMENT '日期',
  `YWY` varchar(255) DEFAULT NULL COMMENT '业务员',
  `THFS` varchar(255) DEFAULT NULL COMMENT '提货方式',
  `beizhu` varchar(255) DEFAULT NULL COMMENT '备注',
  `ddlx` varchar(255) DEFAULT NULL COMMENT '订单类型',
  `yzid` varchar(255) DEFAULT NULL COMMENT '业主内码',
  `DJ_SORT` int(11) DEFAULT '0' COMMENT '单据行号',
  `SPID` varchar(255) DEFAULT NULL COMMENT '商品内码',
  `SL` varchar(255) DEFAULT NULL COMMENT '数量',
  `JS` decimal(14,2) DEFAULT '0.00' COMMENT '件数',
  `LSS` decimal(14,2) DEFAULT '0.00' COMMENT '零散数',
  `PH` varchar(255) DEFAULT NULL COMMENT '批号',
  `lastmodifytime` varchar(255) DEFAULT NULL COMMENT '上传单据时间',
  `RQ_SC` varchar(255) DEFAULT NULL COMMENT '生产日期',
  `YXQZ` varchar(255) DEFAULT NULL COMMENT '有效期至',
  `recnum` int(11) DEFAULT '0',
  `is_zx` int(11) DEFAULT '0' COMMENT '状态',
  `ZCQMC` varchar(255) DEFAULT NULL COMMENT '暂存区名称',
  `ZXS` varchar(255) DEFAULT NULL COMMENT '总箱数',
  `FUHR` varchar(255) DEFAULT NULL COMMENT '复核人',
  `BILLSN` int(11) DEFAULT '0' COMMENT '原单据行号（销售订单行号）',
  PRIMARY KEY (`BillID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of int_wms_xsck_bill
-- ----------------------------
