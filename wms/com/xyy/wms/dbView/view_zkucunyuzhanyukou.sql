
--root 权限下使用
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`%` SQL SECURITY DEFINER VIEW `view_zkucunyuzhanyukou` AS SELECT
	sum(`v`.`a`) AS `a`,
	sum(`v`.`b`) AS `b`,
	sum(`v`.`c`) AS `c`,
	sum(`v`.`d`) AS `d`,
	sum(`v`.`e`) AS `e`,
	sum(`v`.`f`) AS `f`,
	sum(`v`.`g`) AS `g`,
	`v`.`orgId1` AS `orgId1`,
	`v`.`cangkuId1` AS `cangkuId1`,
	`v`.`goodsid1` AS `goodsid1`,
	`v`.`pihaoId1` AS `pihaoId1`,
	`v`.`huoweiId1` AS `huoweiId1`
FROM
	(
		SELECT
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 1
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `a`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 2
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `b`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 3
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `c`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 4
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `d`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 5
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `e`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 6
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `f`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 7
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `g`,
			sum(
				(
					CASE
					WHEN (
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`zuoyeleixing` = 8
					) THEN
						`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`shuliang`
					ELSE
						0
					END
				)
			) AS `h`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`orgId` AS `orgId1`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`cangkuId` AS `cangkuId1`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`goodsid` AS `goodsid1`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`pihaoId` AS `pihaoId1`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`huoweiId` AS `huoweiId1`
		FROM
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`
		GROUP BY
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`orgId`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`cangkuId`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`goodsid`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`pihaoId`,
			`xyy_wms_test`.`xyy_wms_bill_kucunyuzhanyukou`.`huoweiId`
	) `v`
GROUP BY
	`v`.`goodsid1`