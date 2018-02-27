package com.xyy.erp.platform.common.tools;

/**
 * 常量数据字典
 * @author
 */
public abstract class DictKeys {

	/**
	 * URL缓存Key
	 */
	public static final String cache_name_page = "SimplePageCachingFilter";
	
	/**
	 * 系统缓存，主要是权限和数据字典等
	 */
	public static final String cache_name_system = "system";

	/**
	 * 扫描的包
	 */
	public static final String config_scan_package = "config.scan.package";

	/**
	 * 扫描的jar
	 */
	public static final String config_scan_jar = "config.scan.jar";
	
	/**
	 * 开发模式
	 */
	public static final String config_devMode = "config.devMode";
	
	/**
	 * 是否重新构建Lucene索引（构建索引实在是慢）
	 */
	public static final String config_luceneIndex = "config.luceneIndex";
	
	/**
	 * 加密密钥
	 */
	public static final String config_securityKey_key = "config.securityKey";
	
	/**
	 * 密码最大错误次数
	 */
	public static final String config_passErrorCount_key = "config.passErrorCount";
	
	/**
	 * 密码错误最大次数后间隔登陆时间（小时）
	 */
	public static final String config_passErrorHour_key = "config.passErrorHour";

	/**
	 * #文件上传大小限制 10 * 1024 * 1024 = 10M
	 */
	public static final String config_maxPostSize_key = "config.maxPostSize";

	/**
	 * # cookie 值的时间
	 */
	public static final String config_maxAge_key = "config.maxAge";

	/**
	 * # 域名或者服务器IP，多个逗号隔开，验证Referer时使用
	 */
	public static final String config_domain_key = "config.domain";

	/**
	 * mail 配置：邮件服务器地址
	 */
	public static final String config_mail_host = "config.mail.host";

	/**
	 * mail 配置：邮件服务器端口
	 */
	public static final String config_mail_port = "config.mail.port";

	/**
	 * mail 配置：邮件服务器账号
	 */
	public static final String config_mail_from = "config.mail.from";

	/**
	 * mail 配置：邮件服务器名称
	 */
	public static final String config_mail_userName = "config.mail.userName";

	/**
	 * mail 配置：邮件服务器密码
	 */
	public static final String config_mail_password = "config.mail.password";

	/**
	 * mail 配置：接收邮件地址
	 */
	public static final String config_mail_to = "config.mail.to";
	
	/**
	 * 当前数据库类型
	 */
	public static final String db_type_key = "db.type";

	/**
	 * 当前数据库类型：postgresql
	 */
	public static final String db_type_postgresql = "postgresql";

	/**
	 * 当前数据库类型：mysql
	 */
	public static final String db_type_mysql = "mysql";

	/**
	 * 当前数据库类型：oracle
	 */
	public static final String db_type_oracle = "oracle";
	
	/**
	 * 当前数据库类型: sqlserver
	 */
	public static final String db_type_sqlserver = "sqlserver";

	/**
	 * 数据库连接参数：驱动
	 */
	public static final String db_connection_driverClass = "driverClass";
	
	/**
	 * 数据库连接参数：连接URL
	 */
	public static final String db_connection_jdbcUrl = "jdbcUrl";
	
	/**
	 * 数据库连接参数：用户名
	 */
	public static final String db_connection_userName = "userName";
	
	/**
	 * 数据库连接参数：密码
	 */
	public static final String db_connection_passWord = "passWord";

	/**
	 * 数据库连接参数：数据库服务器IP
	 */
	public static final String db_connection_ip = "db_ip";
	
	/**
	 * 数据库连接参数：数据库服务器端口
	 */
	public static final String db_connection_port = "db_port";
	
	/**
	 * 数据库连接参数：数据库名称
	 */
	public static final String db_connection_dbName = "db_name";

	/**
	 * 数据库连接池参数：初始化连接大小
	 */
	public static final String db_initialSize = "db.initialSize";

	/**
	 * 数据库连接池参数：最少连接数
	 */
	public static final String db_minIdle = "db.minIdle";

	/**
	 * 数据库连接池参数：最多连接数
	 */
	public static final String db_maxActive = "db.maxActive";
	
	/**
	 * 数据库连接池参数：初始化连接大小
	 */
	public static final String sqlserverdb_initialSize = "sqlserverdb.initialSize";

	/**
	 * 数据库连接池参数：最少连接数
	 */
	public static final String sqlserverdb_minIdle = "sqlserverdb.minIdle";

	/**
	 * 数据库连接池参数：最多连接数
	 */
	public static final String sqlserverdb_maxActive = "sqlserverdb.maxActive";

	/**
	 *  主数据源名称：系统主数据源
	 */
	public static final String db_dataSource_main = "db.dataSource.main";
	
	/**
	 * 打印中间库数据源
	 */
	public static final String db_dataSource_print_mid = "db.dataSource.print.mid";
	/**
	 * WMS与ERP中间库数据源
	 */
	public static final String db_dataSource_erp_mid = "db.dataSource.erp.mid";
	
	/**
	 * 分页参数初始化值：默认显示第几页
	 */
	public static final int default_pageNumber = 1;
	
	/**
	 * 分页参数初始化值：默认每页显示几多
	 */
	public static final int default_pageSize = 20;
	
	/**
	 * 用户登录状态码：用户不存在
	 */
	public static final int login_info_0 = 0;
	
	/**
	 * 用户登录状态码：停用账户
	 */
	public static final int login_info_1 = 1;
	
	/**
	 * 用户登录状态码：密码错误次数超限
	 */
	public static final int login_info_2 = 2;
	
	/**
	 * 用户登录状态码：密码验证成功
	 */
	public static final int login_info_3 = 3;
	
	/**
	 * 用户登录状态码：密码验证失败
	 */
	public static final int login_info_4 = 4;
	
	/**
	 * 设置字符集
	 */
	public static final String config_encoding = "config.encoding";
	
	/**
	 * 推送消息AppId
	 */
	public static final String app_id = "appId";
	
	/**
	 * 推送消息AppKey
	 */
	public static final String app_key = "appKey";
	
	/**
	 * 推送消息masterSecret
	 */
	public static final String master_secret = "masterSecret";

	/**
	 * 静态资源根路径
	 */
	public static final String app_resource_home="app.resource.home";
	
	/**
	 * 数据库连接参数：驱动
	 */
	public static final String sqlserver_db_connection_driverClass = "sqlserver.driverClass";
	
	/**
	 * 数据库连接参数：连接URL
	 */
	public static final String sqlserver_db_connection_jdbcUrl = "sqlserver.jdbcUrl";
	
	/**
	 * 数据库连接参数：用户名
	 */
	public static final String sqlserver_db_connection_userName = "sqlserver.userName";
	
	/**
	 * 数据库连接参数：密码
	 */
	public static final String sqlserver_db_connection_passWord = "sqlserver.passWord";

	/**
	 * 数据库连接参数：数据库服务器IP
	 */
	public static final String sqlserver_db_connection_ip = "sqlserver_db_ip";
	
	/**
	 * 数据库连接参数：数据库服务器端口
	 */
	public static final String sqlserver_db_connection_port = "sqlserver_db_port";
	
	/**
	 * 数据库连接参数：数据库名称
	 */
	public static final String sqlserver_db_connection_dbName = "sqlserver_db_name";

	/**
	 * 数据库2连接参数：连接URL
	 */
	public static final String sqlserver2_db_connection_jdbcUrl = "sqlserver2.jdbcUrl";
	
	/**
	 * 数据库2连接参数：用户名
	 */
	public static final String sqlserver2_db_connection_userName = "sqlserver2.userName";
	
	/**
	 * 数据库2连接参数：密码
	 */
	public static final String sqlserver2_db_connection_passWord = "sqlserver2.passWord";
	
	/**
	 * 
	 */
	public static final String config_wms_timedtask_flag = "config.wms.timedtask.flag";

	/**
	 * 上传路径path
	 */
	public static final String config_basePath = "config.basePath";
	
	/**
	 * 图片显示路径path
	 */
	public static final String config_urlPath = "config.urlPath";
	/**
	 * EDB系统日志路径
	 */
	public static final String config_edblogPath = "config.edblogPath";
	/**
	 * 同步表名
	 */
	public static final String config_transform_tableNames = "config.transform.tableNames";
	
	public static final String db_dataSource_wms_mid = "db.dataSource.wms.mid";
	/**
	 * ERP数据源
	 */
	public static final String mysql_erp_jdbcUrl = "mysql.erp.jdbcUrl";
	public static final String mysql_erp_userName = "mysql.erp.userName";
	public static final String mysql_erp_passWord = "mysql.erp.passWord";
}
