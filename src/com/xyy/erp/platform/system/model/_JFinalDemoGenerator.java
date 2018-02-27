package com.xyy.erp.platform.system.model;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.xyy.erp.platform.config.launch.JfinalConfig;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _JFinalDemoGenerator {
	
	public static DataSource getDataSource() {
		PropKit.use("a_little_config.txt");
		C3p0Plugin c3p0Plugin = JfinalConfig.createC3p0Plugin();
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.xyy.erp.platform.system.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/com/xyy/erp/platform/system/model/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.xyy.erp.platform.system.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 添加不需要生成的表名
		gernerator.addExcludedTable("tb_sys_user");
		gernerator.addExcludedTable("tb_sys_dept");
		gernerator.addExcludedTable("tb_sys_dept_role_relation");
		gernerator.addExcludedTable("tb_sys_dept_user_relation");
		gernerator.addExcludedTable("tb_sys_permission");
		gernerator.addExcludedTable("tb_sys_role");
		gernerator.addExcludedTable("tb_sys_role_perm_relation");
		gernerator.addExcludedTable("tb_sys_role_user_relation");
		gernerator.addExcludedTable("tb_sys_perm_menu_relation");
		gernerator.addExcludedTable("tb_sys_menu");
		gernerator.addExcludedTable("tb_sys_operation");
		gernerator.addExcludedTable("tb_sys_module");
		gernerator.addExcludedTable("tb_sys_systems");
//		gernerator.addExcludedTable("tb_sys_station");
//		gernerator.addExcludedTable("tb_sys_station_user_relation");
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gernerator.setRemovedTableNamePrefixes("tb_pd_");
		// 生成
		gernerator.generate();
	}
}




