package com.xyy.bill.engine;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.DbPro;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.ChartDataSetDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.def.MultiBillDef;
import com.xyy.bill.def.PrintTplDef;
import com.xyy.bill.def.ReportDef;
import com.xyy.bill.def.ViewportDef;
import com.xyy.bill.engine.TableSchemaUtil.ColumnSchema;
import com.xyy.bill.engine.TableSchemaUtil.TableSchema;
import com.xyy.bill.map.BillMap;
import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.BillViewMeta;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.bill.meta.DataTableMeta.FieldType;
import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.bill.migration.DataMigration;
import com.xyy.bill.migration.MigrationDef;
import com.xyy.bill.print.meta.Print;
import com.xyy.edge.meta.BillEdge;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.util.StringUtil;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;
import com.xyy.util.xml.ComponentWriter;
import com.xyy.util.xml.NotReadableException;

/**
 * 单据引擎
 * 
 * @author evan
 *
 */
public class BillEngine {
	private static BillScanner scanner;
	private static final Log log = Log.getLog(BillEngine.class);

	// 阻塞队列，变化的ds会放入其中
	private BlockingQueue<DataSetMeta> queue = new LinkedBlockingQueue<>();
	// 阻塞队列执行者
	private Executor executor = Executors.newSingleThreadExecutor();
	// 引擎配置文件
	private static final String CONFIG = "/WEB-INF/config/WMS/";
	private String realPath;

	/**
	 * 静态代码块，用于加载系统配置路径
	 */

	/**
	 * 判断当前的操作系统
	 * 
	 * @return
	 */
	public static boolean isOSLinux() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}

	private BillConfig billConfig;

	// 单据定义读写锁
	static ReentrantReadWriteLock rwl_billDefs = new ReentrantReadWriteLock();
	// 报表定义
	private Map<String, BillDef> billDefs = new ConcurrentHashMap<>();

	// 字典定义读写锁
	static ReentrantReadWriteLock rwl_dicDefs = new ReentrantReadWriteLock();
	// 字典定义
	private Map<String, DicDef> dicDefs = new ConcurrentHashMap<>();

	// 多样式表单读写锁
	static ReentrantReadWriteLock rwl_multiBillDefs = new ReentrantReadWriteLock();
	// 多样是表单定义
	private Map<String, MultiBillDef> multiBillDefs = new ConcurrentHashMap<>();

	// 报表定义读写锁
	static ReentrantReadWriteLock rwl_reportDefs = new ReentrantReadWriteLock();
	// 报表定义
	private Map<String, ReportDef> reportDefs = new ConcurrentHashMap<>();

	// 视口定义读写锁
	static ReentrantReadWriteLock rwl_viewportDefs = new ReentrantReadWriteLock();
	// 视口定义
	private Map<String, ViewportDef> viewportDefs = new ConcurrentHashMap<>();

	// 规则定义读写锁
	static ReentrantReadWriteLock rwl_billEdges = new ReentrantReadWriteLock();
	// 规则定义
	private Map<String, BillEdge> billEdges = new ConcurrentHashMap<>();

	// 图表数据集定义读写锁
	static ReentrantReadWriteLock rwl_chartDataSetDefs = new ReentrantReadWriteLock();

	// 图表数据集定义
	private Map<String, ChartDataSetDef> chartDataSetDefs = new ConcurrentHashMap<>();

	private Map<String, BillMap> billMaps = new ConcurrentHashMap<>();
	static ReentrantReadWriteLock rwl_billMaps = new ReentrantReadWriteLock();
	
	//打印模版定义
	private Map<String, PrintTplDef> printTplDefs = new ConcurrentHashMap<>();
	static ReentrantReadWriteLock rwl_printTplMaps = new ReentrantReadWriteLock();
	
	//数据迁移
	private Map<String, MigrationDef> migrationDefs = new ConcurrentHashMap<>();
	static ReentrantReadWriteLock rwl_migrationDefs = new ReentrantReadWriteLock();
	
	
	
	
	
	
	public List<MigrationDef> findMigrationDefs(String srcDataObjectKey){
		List<MigrationDef> result=new ArrayList<>(); 
		for(MigrationDef md:migrationDefs.values()){
			if(md.getDataMigration().getSrcDataObjectKey().equals(srcDataObjectKey)) {
				result.add(md);
			}
		}
		return result;
		
	}

	/**
	 * 获取单据定义
	 * 
	 * @param mapKey
	 * @return
	 */
	public MigrationDef getMigrationDef(String migrationKey){
		rwl_migrationDefs.readLock().lock();
		try {
			return migrationDefs.get(migrationKey);
		} catch (Exception ex) {
			return null;
		} finally {
			// 释放读锁
			rwl_migrationDefs.readLock().unlock();
		}
	}
	
	/**
	 * 获取打印模版定义
	 * @param mapKey
	 * @return
	 */
	public PrintTplDef getPrintTplDef(String tplKey) {
		rwl_printTplMaps.readLock().lock();
		try {
			return printTplDefs.get(tplKey);
		} catch (Exception ex) {
			return null;
		} finally {
			// 释放读锁
			rwl_printTplMaps.readLock().unlock();
		}
	}

	
	
	public BillMap getBillMap(String mapKey) {
		rwl_billMaps.readLock().lock();
		try {
			return billMaps.get(mapKey);
		} catch (Exception ex) {
			return null;
		} finally {
			// 释放读锁
			rwl_billMaps.readLock().unlock();
		}
	}

	// 获取规则定义
	public BillEdge getBillEdge(String edgeKey) {
		rwl_billEdges.readLock().lock();

		try {
			return billEdges.get(edgeKey);
		} catch (Exception ex) {
			return null;
		} finally {
			// 释放读锁
			rwl_billEdges.readLock().unlock();
		}
	}

	// 获取单据定义
	public BillDef getBillDef(String billKey) {
		rwl_billDefs.readLock().lock();
		try {
			return billDefs.get(billKey);
		} catch (Exception ex) {
			return null;
		} finally {
			rwl_billDefs.readLock().unlock();
		}
	}

	// 获取字典定义
	public DicDef getDicDef(String dicKey) {
		rwl_dicDefs.readLock().lock();
		try {
			return dicDefs.get(dicKey);
		} catch (Exception ex) {
			return null;
		} finally {
			rwl_dicDefs.readLock().unlock();
		}
	}

	public MultiBillDef getMultiBillDef(String billKey) {
		rwl_multiBillDefs.readLock().lock();
		try {
			return multiBillDefs.get(billKey);
		} catch (Exception ex) {
			return null;
		} finally {
			rwl_multiBillDefs.readLock().unlock();
		}
	}

	public ReportDef getReportDef(String billKey) {
		rwl_reportDefs.readLock().lock();
		try {
			return reportDefs.get(billKey);
		} catch (Exception ex) {
			return null;
		} finally {
			rwl_reportDefs.readLock().unlock();
		}
	}

	public ViewportDef getViewportDef(String billKey) {
		rwl_viewportDefs.readLock().lock();
		try {
			return viewportDefs.get(billKey);
		} catch (Exception ex) {
			return null;
		} finally {
			rwl_viewportDefs.readLock().unlock();
		}
	}

	public ChartDataSetDef getChartDataSetDef(String key) {
		rwl_chartDataSetDefs.readLock().lock();
		try {
			return chartDataSetDefs.get(key);
		} catch (Exception ex) {
			return null;
		} finally {
			rwl_chartDataSetDefs.readLock().unlock();
		}
	}

	public BillConfig getBillConfig() {
		return billConfig;
	}

	public BillEngine() {

	}

	// 初始化引擎
	public void init() {
		/**
		 * 根据配置文件，加载系统初始配置
		 */
		this.initConfig();
		/**
		 * 初始化后端扫面器，监控配置文件的变化
		 */
		this.initScanner();
	}

	private void initScanner() {
		scanner = new BillScanner(realPath, 5) {
			/**
			 * 配置产生变化，需要重新加载配置
			 */
			@Override
			public void onChange(Map<String, TimeSize> pre, Map<String, TimeSize> cur) {
				Map<String, TimeSize> changed = new HashMap<>();
				// 分析变化部分
				for (String file : cur.keySet()) {
					if (!pre.containsKey(file) || !cur.get(file).equals(pre.get(file))) {// 新增或修改的配置
						changed.put(file, cur.get(file));// 变化列表
					}
				}
				// 加载配置--变化的部分
				BillEngine.this.reloadConfig(changed);
			}

			/**
			 * 数据集文件有变化 （1）当前文件是新增加的==> !preDS.containsKey(file) （2）当前文件改变了 ==>
			 * !curDS.get(file).equals(preDS.get(file))
			 */
			@Override
			public void onDSChange(Map<String, TimeSize> preDS, Map<String, TimeSize> curDS) {
				// 遍历数据集，如果产生变化，则将数据集的变化放入到队列中，等待处理
				for (String file : curDS.keySet()) {
					if (!preDS.containsKey(file) || !curDS.get(file).equals(preDS.get(file))) {
						try {
							if (file.contains("Dic_")) {// 字典
								BillEngine.this.queue.put(
										BillEngine.this.dicDefs.get(getKeyFromAbstractFileName(file)).getDataSet());
							} else if (file.contains("Report_")) {// 报表
								BillEngine.this.queue.put(
										BillEngine.this.reportDefs.get(getKeyFromAbstractFileName(file)).getDataSet());
							} else if (file.contains("MultiBill_")) {// 多样式表单
								BillEngine.this.queue.put(BillEngine.this.multiBillDefs
										.get(getKeyFromAbstractFileName(file)).getDataSet());
							} else if (file.contains("Viewport_")) {// 视图
								BillEngine.this.queue.put(BillEngine.this.viewportDefs
										.get(getKeyFromAbstractFileName(file)).getDataSet());
							} else if (file.contains("Datasource_")) {// 圖表
								BillEngine.this.queue.put(BillEngine.this.chartDataSetDefs
										.get(getKeyFromAbstractFileName(file)).getDataSet());
							} else if(file.contains("Migration_")){//迁移表
								BillEngine.this.queue.put(BillEngine.this.migrationDefs
										.get(getKeyFromAbstractFileName(file)).getDataSet());
							}else {
								BillEngine.this.queue.put(
										BillEngine.this.billDefs.get(getKeyFromAbstractFileName(file)).getDataSet());
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}

			/**
			 * 从全路径中获取文件数据集的key
			 * 
			 * @param file
			 * @return
			 */
			private String getKeyFromAbstractFileName(String file) {
				String type = "";
				String substring = "";
				if (file.contains("Dic_")) {
					substring = file.substring(file.indexOf("Dic_") + 4, file.indexOf("_DS"));
					type = "Dictionary";
				} else if (file.contains("Report_")) {
					substring = file.substring(file.indexOf("Report_") + 7, file.indexOf("_DS"));
					type = "Report";
				} else if (file.contains("MultiBill_")) {
					substring = file.substring(file.indexOf("MultiBill_") + 10, file.indexOf("_DS"));
					type = "MultiBill";
				} else if (file.contains("Viewport_")) {
					substring = file.substring(file.indexOf("Viewport_") + 9, file.indexOf("_DS"));
					type = "Viewport";
				} else if (file.contains("Datasource_")) {
					type = "Datasource";
					substring = file.substring(file.indexOf("Datasource_") + 11, file.indexOf("_DS"));
				}else if(file.contains("Migration_")){
					type = "Migration";
					substring = file.substring(file.indexOf("Migration_") + 10, file.indexOf("_DS"));
				} else {
					type = "Bill";
					substring = file.substring(file.indexOf("Bill_") + 5, file.indexOf("_DS"));
				}
				File dir = new File(realPath + type);
				substring = getChangedFile(file, dir).get("key") + substring;
				return substring;
			}

			// 扫描器初始化
			@Override
			public void onInitScanner() {
				System.out.println("XYY-bill-scanner-init...");
			}

		};
		// 启动扫描器
		scanner.start();
		/**
		 * 开启数据元监控线程，监控数据schema的变化
		 */
		this.executor.execute(new DSMonitor());// 开启数据监控线程

	}

	private void reloadConfig(Map<String, TimeSize> changed) {
		// 加载配置文件
		this.scanConfig(realPath);
		// scanBill
		this.scanBill(realPath, "Bill", changed);
		// scanDic
		this.scanDic(realPath, "Dictionary", changed);
		// scanReport
		this.scanReport(realPath, "Report", changed);
		// scanMultiBill
		this.scanMultiBill(realPath, "MultiBill", changed);
		// scanEdge
		this.scanEdge(realPath, "Edge", changed);
		// scanViewport
		this.scanViewport(realPath, "Viewport", changed);
		// scanPrintTpl
		this.scanPrintTpl(realPath, "Print", changed);
		
		// scanChart
		this.scanChart(realPath, "Datasource", changed);
		// map
		this.scanMap(realPath, "Map", changed);
		// Migration 数据迁移
		this.scanMigration(realPath, "Migration", changed);
		// update config
		this.upateConfig(changed);
	}

	private void scanMigration(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_migrationDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptMigration(map, changed, dir);
		} finally {
			rwl_migrationDefs.writeLock().unlock();
		}
	}

	private void acceptMigration(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Migration_")) {
					// 规则
					if (name.startsWith("Migration_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadMigration(file, key + name.substring(10, name.length() - 4));
					}
					// 规则数据源
					if (name.startsWith("Migration_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadMigrationDataSet(file, key + name.substring(10, name.length() - 7));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Migration_")) {
						// 规则
						if (name.startsWith("Migration_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadMigration(file, key + name.substring(10, name.length() - 4));
						}
						// 规则数据源
						if (name.startsWith("Migration_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadMigrationDataSet(file, key + name.substring(10, name.length() - 7));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("bill config dir not found.");
				}
			}
		}
		acceptMigration(mapFile, changed, dir);
	}

	private void loadMigration(File file, String key) {
		log.info("load Migration def:" + key);
		DataMigration result = null;
		ComponentReader cr = new ComponentReader(DataMigration.class);
		try {
			result = (DataMigration) cr.readXML(XMLUtil.readXMLFile(file));

			MigrationDef migrationDef = this.migrationDefs.get(key);
			if (migrationDef == null) {
				migrationDef = new MigrationDef(key);
				this.migrationDefs.put(key, migrationDef);
			}
			String fileName = file.getName();
			migrationDef.setFullKey(fileName.substring(0, fileName.length() - 4));
			migrationDef.setDataMigration(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load Migration def error:" + key);
		}
	}
	
	private void loadMigrationDataSet(File file, String key) {
		log.info("load Migration dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));
			List<DataTableMeta> dtmList = result.getDataTableMetas();
			for (DataTableMeta dtm : dtmList) {
				// 加载系统字段
				this.addMigrationSysItem(dtm);
			}

			MigrationDef migrationDef = this.migrationDefs.get(key);
			if (migrationDef == null) {
				migrationDef = new MigrationDef(key);
				this.migrationDefs.put(key, migrationDef);
			}
			String fileName = file.getName();
			migrationDef.setFullKey(fileName.substring(0, fileName.length() - 7));
			migrationDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load migration dataset def error:" + key);
		}
	}


	private void scanMap(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_chartDataSetDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptMap(map, changed, dir);
		} finally {
			rwl_chartDataSetDefs.writeLock().unlock();
		}
	}

	private void acceptMap(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Map_")) {
					BillEngine.this.loadMap(file, key + name.substring(4, name.length() - 4));
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Map_")) {
							BillEngine.this.loadMap(file, key + name.substring(4, name.length() - 4));
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("MAP config dir not found.");
				}
			}
		}
		acceptMap(mapFile, changed, dir);
	}

	private void loadMap(File file, String key) {
		log.info("load map def:" + key);
		BillMap result = null;
		ComponentReader cr = new ComponentReader(BillMap.class);
		try {
			result = (BillMap) cr.readXML(XMLUtil.readXMLFile(file));
			billMaps.put(key, result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load viewport def error:" + key);
		}
	}

	/**
	 * 扫描图表控件
	 * 
	 * @param path
	 * @param dirName
	 * @param changed
	 */
	private void scanChart(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_chartDataSetDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptChart(map, changed, dir);
		} finally {
			rwl_chartDataSetDefs.writeLock().unlock();
		}
	}

	/**
	 * 加载图表文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptChart(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Datasource_")) {
					// 视口数据源
					if (name.startsWith("Datasource_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadChartDataSet(file, key + name.substring(11, name.length() - 7));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Datasource_")) {
						// 视口数据源
						if (name.startsWith("Datasource_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadChartDataSet(file, key + name.substring(11, name.length() - 7));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("chart config dir not found.");
				}
			}
		}
		acceptChart(mapFile, changed, dir);
	}

	protected void loadChartDataSet(File file, String key) {
		log.info("load chart dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));
			ChartDataSetDef chartBillDef = this.chartDataSetDefs.get(key);
			if (chartBillDef == null) {
				chartBillDef = new ChartDataSetDef(key);
				this.chartDataSetDefs.put(key, chartBillDef);
			}
			String fileName = file.getName();
			chartBillDef.setFullKey(fileName.substring(0, fileName.length() - 7));
			chartBillDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load viewport dataset def error:" + key);
		}
	}

	/**
	 * 更新配置文件
	 */
	private void upateConfig(Map<String, TimeSize> changed) {
		boolean flag = false;
		if (changed != null) {
			for (String file : changed.keySet()) {
				if (file.indexOf("Bill_") > 0 || file.indexOf("Dic_") > 0 || file.indexOf("MultiBill_") > 0) {
					flag = true;
				}
			}
		}
		// 加载单据
		for (String billKey : this.billDefs.keySet()) {
			if (this.billConfig == null) {
				break;
			}
			this.billConfig.addBillDef(this.billDefs.get(billKey));
		}
		// 加载多样式表单
		for (String billKey : this.multiBillDefs.keySet()) {
			if (this.billConfig == null) {
				break;
			}
			this.billConfig.addmultiBills(this.multiBillDefs.get(billKey));
		}
		// 加载字典
		for (String billKey : this.dicDefs.keySet()) {
			if (this.billConfig == null) {
				break;
			}
			this.billConfig.addDics(this.dicDefs.get(billKey));
		}
		if (flag) {
			ComponentWriter cw = new ComponentWriter(this.billConfig.getClass());
			try {
				XMLUtil.save(cw.writeXML(this.billConfig), realPath + "/config.xml");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void scanViewport(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_viewportDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptViewport(map, changed, dir);
		} finally {
			rwl_viewportDefs.writeLock().unlock();
		}
	}
	
	
	private void scanPrintTpl(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_printTplMaps.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptPrintTpl(map, changed, dir);
		} finally {
			rwl_printTplMaps.writeLock().unlock();
		}
	}


	/**
	 * 加载视图文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptViewport(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Viewport_")) {
					// 视口
					if (name.startsWith("Viewport_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadViewport(file, key + name.substring(9, name.length() - 4));
					}
					// 视口数据源
					if (name.startsWith("Viewport_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadViewportDataSet(file, key + name.substring(9, name.length() - 7));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Viewport_")) {
						// 视口
						if (name.startsWith("Viewport_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadViewport(file, key + name.substring(9, name.length() - 4));
						}
						// 视口数据源
						if (name.startsWith("Viewport_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadViewportDataSet(file, key + name.substring(9, name.length() - 7));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("Viewport config dir not found.");
				}
			}
		}
		acceptViewport(mapFile, changed, dir);
	}

	
	/**
	 * 加载打印模版
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptPrintTpl(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Print_")) {
					BillEngine.this.loadPrintTpl(file, key + name.substring(6,name.length()-4));
					
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Print_")) {
						BillEngine.this.loadPrintTpl(file, key + name.substring(6,name.length()-4));
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("PrintTpl config dir not found.");
				}
			}
		}
		
		acceptPrintTpl(mapFile, changed, dir);
	}
	
	/**
	 * 加载打印模版
	 * @param file
	 * @param string
	 */
	private void loadPrintTpl(File file, String key) {
		log.info("load printtpl def:" + key);
		Print result = null;
		ComponentReader cr = new ComponentReader(Print.class);
		try {
			result = (Print) cr.readXML(XMLUtil.readXMLFile(file));
			PrintTplDef printTplDef = this.printTplDefs.get(key);
			if (printTplDef == null) {
				printTplDef = new PrintTplDef(key);
				this.printTplDefs.put(key, printTplDef);
			}
			printTplDef.setPrint(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load printtpl def error:" + key);
		}
		
	}

	protected void loadViewport(File file, String key) {
		log.info("load viewport def:" + key);
		BillViewMeta result = null;
		ComponentReader cr = new ComponentReader(BillViewMeta.class);
		try {
			result = (BillViewMeta) cr.readXML(XMLUtil.readXMLFile(file));
			ViewportDef viewportDef = this.viewportDefs.get(key);
			if (viewportDef == null) {
				viewportDef = new ViewportDef(key);
				this.viewportDefs.put(key, viewportDef);
			}
			String fileName = file.getName();
			viewportDef.setFullKey(fileName.substring(0, fileName.length() - 4));
			viewportDef.setView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load viewport def error:" + key);
		}

	}

	protected void loadViewportDataSet(File file, String key) {
		log.info("load viewport dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));
			ViewportDef viewportDef = this.viewportDefs.get(key);
			if (viewportDef == null) {
				viewportDef = new ViewportDef(key);
				this.viewportDefs.put(key, viewportDef);
			}
			String fileName = file.getName();
			viewportDef.setFullKey(fileName.substring(0, fileName.length() - 7));
			viewportDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load viewport dataset def error:" + key);
		}

	}

	private void scanConfig(String dirName) {
		ComponentReader cr = new ComponentReader(BillConfig.class);
		try {
			this.billConfig = (BillConfig) cr.readXML(XMLUtil.readXMLFile(dirName + "/config.xml"));
		} catch (NotReadableException e) {
			e.printStackTrace();
		}
	}

	private void initConfig() {
		this.reloadConfig(null);
	}

	/**
	 * 扫描规则文件
	 * 
	 * @param path
	 * @param dirName
	 */
	private void scanEdge(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_billEdges.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptEdge(map, changed, dir);
		} finally {
			rwl_billEdges.writeLock().unlock();
		}

	}

	/**
	 * 加载规则文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptEdge(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.indexOf("_edge") > 0) {
					BillEngine.this.loadEdge(file, key + name.split("_")[2]);
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.indexOf("_edge") > 0) {
						BillEngine.this.loadEdge(file, key + name.split("_")[2]);
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("edge config dir not found.");
				}
			}
		}
		acceptEdge(mapFile, changed, dir);
	}

	protected void loadEdge(File file, String key) {
		log.info("load dic dataset def:" + key);
		BillEdge result = null;
		ComponentReader cr = new ComponentReader(BillEdge.class);
		try {
			result = (BillEdge) cr.readXML(XMLUtil.readXMLFile(file));
			if (result != null) {
				this.billEdges.put(key, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load dic dataset def error:" + key);
		}

	}

	/**
	 * 扫面并加载单据定义
	 * 
	 * @param path
	 * @param dirName
	 */
	private void scanBill(String path, String dirName, Map<String, TimeSize> changed) {
		// 写锁
		rwl_billDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptBill(map, changed, dir);
		} finally {
			// 释放写锁
			rwl_billDefs.writeLock().unlock();
		}

	}

	/**
	 * 获取文件补全key
	 * 
	 * @param fileName
	 * @param dir
	 * @return
	 */
	private Map<String, String> getChangedFile(String fileName, File dir) {
		Map<String, String> map = new HashMap<>();
		File file = new File(fileName);
		String name = file.getName();
		map.put("name", name);
		String dirPath = dir.getPath() + "\\";
		fileName = fileName.replace(dirPath, "");
		fileName = fileName.replace("\\", "_");
		fileName = fileName.replace(name, "");
		map.put("key", fileName);
		return map;
	}

	/**
	 * 加载单据文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptBill(Map<String, File> map, Map<String, TimeSize> changed, File dir) {

		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Bill_") || name.startsWith("View_")) {
					// 单据
					if (name.startsWith("Bill_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadBill(file, key + name.substring(5, name.length() - 4));
					}
					// 单据数据源
					if (name.startsWith("Bill_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadBillDataSet(file, key + name.substring(5, name.length() - 7));
					}
					// 单据叙事薄
					if (name.startsWith("View_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadBillListView(file, key + name.substring(5, name.length() - 4));
					}
					// 叙事薄数据源
					if (name.startsWith("View_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadBillListViewDataSet(file, key + name.substring(5, name.length() - 7));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Bill_") || name.startsWith("View_")) {
						// 单据
						if (name.startsWith("Bill_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadBill(file, key + name.substring(5, name.length() - 4));
						}
						// 单据数据源
						if (name.startsWith("Bill_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadBillDataSet(file, key + name.substring(5, name.length() - 7));
						}
						// 单据叙事薄
						if (name.startsWith("View_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadBillListView(file, key + name.substring(5, name.length() - 4));
						}
						// 叙事薄数据源
						if (name.startsWith("View_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadBillListViewDataSet(file, key + name.substring(5, name.length() - 7));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("bill config dir not found.");
				}
			}
		}
		acceptBill(mapFile, changed, dir);
	}

	/**
	 * 加载字典文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptDic(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Dic_")) {
					// 单据
					if (name.startsWith("Dic_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadDic(file, key + name.substring(4, name.length() - 4));
					}
					// 单据数据源
					if (name.startsWith("Dic_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadDicDataSet(file, key + name.substring(4, name.length() - 7));
					}
					// 字典列表
					if (name.startsWith("Dic_") && name.indexOf("List") > 0 && name.indexOf("_DS") < 0) {
						BillEngine.this.loadDicListView(file, key + name.substring(4, name.lastIndexOf("List") - 1));
					}
					// 字典列表数据源
					if (name.startsWith("Dic_") && name.indexOf("List") > 0 && name.indexOf("_DS") > 0) {
						BillEngine.this.loadDicListViewDataSet(file,
								key + name.substring(4, name.lastIndexOf("List") - 1));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Dic_")) {
						// 单据
						if (name.startsWith("Dic_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadDic(file, key + name.substring(4, name.length() - 4));
						}
						// 单据数据源
						if (name.startsWith("Dic_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadDicDataSet(file, key + name.substring(4, name.length() - 7));
						}
						// 字典列表
						if (name.startsWith("Dic_") && name.indexOf("List") > 0 && name.indexOf("_DS") < 0) {
							BillEngine.this.loadDicListView(file,
									key + name.substring(4, name.lastIndexOf("List") - 1));
						}
						// 字典列表数据源
						if (name.startsWith("Dic_") && name.indexOf("List") > 0 && name.indexOf("_DS") > 0) {
							BillEngine.this.loadDicListViewDataSet(file,
									key + name.substring(4, name.lastIndexOf("List") - 1));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("dic config dir not found.");
				}
			}
		}
		acceptDic(mapFile, changed, dir);
	}

	/**
	 * 扫描指定定义
	 * 
	 * @param path
	 * @param dirName
	 */
	private void scanDic(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_dicDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptDic(map, changed, dir);
		} finally {
			rwl_dicDefs.writeLock().unlock();
		}

	}

	/**
	 * 加载字典列表数据源
	 * 
	 * @param file
	 * @param substring
	 */
	protected void loadDicListViewDataSet(File file, String key) {
		log.info("load bill listview dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));

			DicDef dicDef = this.dicDefs.get(key);
			if (dicDef == null) {
				dicDef = new DicDef(key);
				this.dicDefs.put(key, dicDef);
			}
			dicDef.setDicListDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load dic listview  dataset def error:" + key);
		}
	}

	/**
	 * 加载字典列表视图
	 * 
	 * @param file
	 * @param substring
	 */
	protected void loadDicListView(File file, String key) {
		log.info("load dic listview def:" + key);
		BillFormMeta result = null;
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			result = (BillFormMeta) cr.readXML(XMLUtil.readXMLFile(file));

			DicDef dicDef = this.dicDefs.get(key);
			if (dicDef == null) {

				dicDef = new DicDef(key);
				dicDef.setFullKey(file.getName().substring(0, file.getName().length() - 4));
				this.dicDefs.put(key, dicDef);
			}
			dicDef.setDicListView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load dic listview def error:" + key);
		}
	}

	protected void loadDicDataSet(File file, String key) {
		log.info("load dic dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));

			List<DataTableMeta> dtmList = result.getDataTableMetas();
			for (DataTableMeta dtm : dtmList) {
				// 单据
				this.addDicSysItem(dtm);
			}

			DicDef dicDef = this.dicDefs.get(key);
			if (dicDef == null) {
				dicDef = new DicDef(key);
				this.dicDefs.put(key, dicDef);
			}
			String fileName = file.getName();
			dicDef.setFullKey(fileName.substring(0, fileName.length() - 7));

			dicDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load dic dataset def error:" + key);
		}

	}

	protected void loadDic(File file, String key) {
		log.info("load dic def:" + key);
		BillFormMeta result = null;
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			result = (BillFormMeta) cr.readXML(XMLUtil.readXMLFile(file));

			DicDef dicDef = this.dicDefs.get(key);
			if (dicDef == null) {
				dicDef = new DicDef(key);
				this.dicDefs.put(key, dicDef);
			}
			String fileName = file.getName();
			dicDef.setFullKey(fileName.substring(0, fileName.length() - 4));
			dicDef.setView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load dic def error:" + key);
		}

	}

	/**
	 * 扫描报表定义
	 * 
	 * @param path
	 * @param dirName
	 */
	private void scanReport(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_reportDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptReport(map, changed, dir);
		} finally {
			rwl_reportDefs.writeLock().unlock();
		}

	}

	/**
	 * 加载报表文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptReport(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("Report_")) {
					// 单据
					if (name.startsWith("Report_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadReport(file, key + name.substring(7, name.length() - 4));
					}
					// 单据数据源
					if (name.startsWith("Report_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadReportDataSet(file, key + name.substring(7, name.length() - 7));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("Report_")) {
						// 单据
						if (name.startsWith("Report_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadReport(file, key + name.substring(7, name.length() - 4));
						}
						// 单据数据源
						if (name.startsWith("Report_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadReportDataSet(file, key + name.substring(7, name.length() - 7));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("report config dir not found.");
				}
			}
		}
		acceptReport(mapFile, changed, dir);
	}

	protected void loadReportDataSet(File file, String key) {
		log.info("load report dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));
			ReportDef reportDef = this.reportDefs.get(key);
			if (reportDef == null) {
				reportDef = new ReportDef(key);
				this.reportDefs.put(key, reportDef);
			}
			String fileName = file.getName();
			reportDef.setFullKey(fileName.substring(0, fileName.length() - 7));
			reportDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load report dataset def error:" + key);
		}
	}

	protected void loadReport(File file, String key) {
		log.info("load report def:" + key);
		BillFormMeta result = null;
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			result = (BillFormMeta) cr.readXML(XMLUtil.readXMLFile(file));

			ReportDef reportDef = this.reportDefs.get(key);
			if (reportDef == null) {
				reportDef = new ReportDef(key);
				this.reportDefs.put(key, reportDef);
			}
			String fileName = file.getName();
			reportDef.setFullKey(fileName.substring(0, fileName.length() - 4));
			reportDef.setView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load report def error:" + key);
		}

	}

	/**
	 * 扫描多样式表单定义
	 * 
	 * @param path
	 * @param dirName
	 */
	private void scanMultiBill(String path, String dirName, Map<String, TimeSize> changed) {
		rwl_multiBillDefs.writeLock().lock();
		try {
			File dir = new File(path + dirName + "/");
			Map<String, File> map = new HashMap<>();
			map.put("", dir);
			acceptMultiBill(map, changed, dir);
		} finally {
			rwl_multiBillDefs.writeLock().unlock();
		}

	}

	/**
	 * 加载多样式表单文件
	 * 
	 * @param map
	 * @param changed
	 * @param dir
	 */
	private void acceptMultiBill(Map<String, File> map, Map<String, TimeSize> changed, File dir) {
		if (map.isEmpty())
			return;
		if (changed != null) {
			for (String changeKey : changed.keySet()) {
				Map<String, String> mapChange = getChangedFile(changeKey, dir);
				String name = mapChange.get("name");
				String key = mapChange.get("key");
				File file = new File(changeKey);
				if (name.startsWith("MultiBill_")) {
					// 单据
					if (name.startsWith("MultiBill_") && name.indexOf("_DS") < 0) {
						BillEngine.this.loadMultiBill(file, key + name.substring(10, name.length() - 4));
					}
					// 单据数据源
					if (name.startsWith("MultiBill_") && name.indexOf("_DS") > 0) {
						BillEngine.this.loadMultiBillDataSet(file, key + name.substring(10, name.length() - 7));
					}
				}
			}
			return;
		}
		Map<String, File> mapFile = new HashMap<>();
		for (String key : map.keySet()) {
			File fileMap = map.get(key);
			File[] fileList = fileMap.listFiles();
			for (File file : fileList) {
				if (file.exists() && file.isFile()) {
					String name = file.getName();
					if (name.startsWith("MultiBill_")) {
						// 单据
						if (name.startsWith("MultiBill_") && name.indexOf("_DS") < 0) {
							BillEngine.this.loadMultiBill(file, key + name.substring(10, name.length() - 4));
						}
						// 单据数据源
						if (name.startsWith("MultiBill_") && name.indexOf("_DS") > 0) {
							BillEngine.this.loadMultiBillDataSet(file, key + name.substring(10, name.length() - 7));
						}
					}
				} else if (file.exists() && file.isDirectory()) {
					String fileKey = "";
					fileKey = key + file.getName() + "_";
					mapFile.put(fileKey, file);
				} else {
					log.error("multiBill config dir not found.");
				}
			}
		}
		acceptMultiBill(mapFile, changed, dir);
	}

	protected void loadMultiBillDataSet(File file, String key) {
		log.info("load multibill dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));
			MultiBillDef multiBillDef = this.multiBillDefs.get(key);
			if (multiBillDef == null) {
				multiBillDef = new MultiBillDef(key);
				this.multiBillDefs.put(key, multiBillDef);
			}
			String fileName = file.getName();
			multiBillDef.setFullKey(fileName.substring(0, fileName.length() - 7));
			multiBillDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load multibill dataset def error:" + key);
		}

	}

	protected void loadMultiBill(File file, String key) {
		log.info("load multibill def:" + key);
		BillFormMeta result = null;
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			result = (BillFormMeta) cr.readXML(XMLUtil.readXMLFile(file));

			MultiBillDef multiBillDef = this.multiBillDefs.get(key);
			if (multiBillDef == null) {
				multiBillDef = new MultiBillDef(key);
				this.multiBillDefs.put(key, multiBillDef);
			}
			String fileName = file.getName();
			multiBillDef.setFullKey(fileName.substring(0, fileName.length() - 4));
			multiBillDef.setView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load multibill def error:" + key);
		}

	}

	/**
	 * 加载单据叙事薄数据集
	 * 
	 * @param file
	 * @param key
	 */
	protected void loadBillListViewDataSet(File file, String key) {
		log.info("load bill listview dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));

			BillDef billDef = this.billDefs.get(key);
			if (billDef == null) {
				billDef = new BillDef(key);
				this.billDefs.put(key, billDef);
			}
			billDef.setBillListDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load bill listview  dataset def error:" + key);
		}
	}

	/**
	 * 加载单据叙事薄数据
	 * 
	 * @param file
	 * @param key
	 */
	protected void loadBillListView(File file, String key) {
		log.info("load bill listview def:" + key);
		BillFormMeta result = null;
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			result = (BillFormMeta) cr.readXML(XMLUtil.readXMLFile(file));

			BillDef billDef = this.billDefs.get(key);
			if (billDef == null) {

				billDef = new BillDef(key);
				billDef.setFullKey(file.getName().substring(0, file.getName().length() - 4));
				this.billDefs.put(key, billDef);
			}
			billDef.setBillListView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load bill listview def error:" + key);
		}
	}

	/**
	 * 加载Bill数据集定义
	 * 
	 * @param file
	 * @param key
	 */
	protected void loadBillDataSet(File file, String key) {
		log.info("load bill dataset def:" + key);
		DataSetMeta result = null;
		ComponentReader cr = new ComponentReader(DataSetMeta.class);
		try {
			result = (DataSetMeta) cr.readXML(XMLUtil.readXMLFile(file));
			List<DataTableMeta> dtmList = result.getDataTableMetas();
			for (DataTableMeta dtm : dtmList) {
				// 加载系统字段
				this.addBillSysItem(dtm);
			}

			BillDef billDef = this.billDefs.get(key);
			if (billDef == null) {
				billDef = new BillDef(key);
				this.billDefs.put(key, billDef);
			}
			String fileName = file.getName();
			billDef.setFullKey(fileName.substring(0, fileName.length() - 7));
			billDef.setDataSet(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load bill dataset def error:" + key);
		}
	}

	/**
	 * 加载bill定义
	 * 
	 * @param file
	 *            ,bill定义文件
	 * @param key
	 *            ，bill定义key
	 * 
	 */
	protected void loadBill(File file, String key) {
		log.info("load bill def:" + key);
		System.out.println(file.exists());
		BillFormMeta result = null;
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			result = (BillFormMeta) cr.readXML(XMLUtil.readXMLFile(file));
			BillDef billDef = this.billDefs.get(key);
			if (billDef == null) {
				billDef = new BillDef(key);
				this.billDefs.put(key, billDef);
			}
			String fileName = file.getName();
			billDef.setFullKey(fileName.substring(0, fileName.length() - 4));
			billDef.setView(result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("load bill def error:" + key);
		}

	}

	public static String getConfig() {
		return CONFIG;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
		if (".".equals(realPath)) {
			// 查询路径
			String classPath = BillEngine.class.getResource("/").toString();
			int idx = classPath.indexOf("/WEB-INF/");

			this.realPath = classPath.substring(6, idx) + getConfig();
			if (isOSLinux()) {
				this.realPath = "/" + this.realPath;
			}
			System.out.println("===================classpath===============================");
			System.out.println("classpaht=" + classPath);
			System.out.println("realPath=" + this.realPath);
			System.out.println("===================classpath===============================");
		}
	}

	class DSMonitor implements Runnable {

		public DSMonitor() {
			super();

		}

		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					e.printStackTrace();
				}
			});
			while (true) {
				try {
					// 获取数据
					DataSetMeta dataSetMeta = BillEngine.this.queue.take();
					boolean devMode = Boolean.valueOf(PropertiesPlugin.getParamMapValue(DictKeys.config_devMode).toString());
					if(devMode){
						String ds = dataSetMeta.getDataSource();
						// 处理表
						for (DataTableMeta table : dataSetMeta.getDataTableMetas()) {
							// 只处理表结构部分哟
							if (!table.getTableName().startsWith("Table::")) {
								continue;
							}
							TableSchema tableSchema = BillEngine.this.tableExists(table, ds);
							if (tableSchema != null) {// 判断标识是否存在
								BillEngine.this.alterTable(table, ds, tableSchema);
							} else {// create table
								BillEngine.this.createTable(table, ds);
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * 判断表是否存在
	 * 
	 * @param table
	 * @param ds
	 * @return
	 */
	public TableSchema tableExists(DataTableMeta table, String dataSetDataSource) {
		String ds = table.getDataSource();
		if (StringUtil.isEmpty(ds)) {// 局部数据源优先
			ds = dataSetDataSource;
		}
		@SuppressWarnings("unused")
		DbPro dbPro = null;
		if (StringUtil.isEmpty(ds)) {
			dbPro = Db.use();
		} else {
			dbPro = Db.use(ds);
		}
		try {
			TableSchemaUtil util = new TableSchemaUtil(DbKit.getConfig().getConnection());
			return util.querySchema(table.getRealTableName());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 创建表
	 * 
	 * @param table
	 * @param dataSetDataSource
	 */
	public void createTable(DataTableMeta table, String dataSetDataSource) {

		String create_table_sql = table.getCreateSQL();
		String ds = table.getDataSource();
		if (StringUtil.isEmpty(ds)) {// 局部数据源优先
			ds = dataSetDataSource;
		}
		DbPro dbPro = null;
		if (StringUtil.isEmpty(ds)) {
			dbPro = Db.use();
		} else {
			dbPro = Db.use(ds);
		}
		try {
			dbPro.update(create_table_sql);// 执行创建语句
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private DataTableMeta addMigrationSysItem(DataTableMeta table){
		List<Field> systemFields = new ArrayList<>();
		systemFields.add(new Field("OID", "OID", FieldType.OID, null, "主键ID", null, false, null));
//			systemFields.add(new Field("auditStatus", "审核状态", FieldType.Int, null, "审核状态", null, false, null));
		systemFields.add(new Field("orgId", "机构ID", FieldType.Varchar, null, "机构ID", null, false, null));
		systemFields.add(new Field("orgCode", "机构编码", FieldType.Varchar, null, "机构编码", null, false, null));
		systemFields.add(new Field("createTime", "创建时间", FieldType.TimeStamp, null, "创建时间", null, false, null));
		systemFields.add(new Field("updateTime", "更新时间", FieldType.TimeStamp, null, "更新时间", null, false, null));
		systemFields.add(new Field("creator", "创建人ID", FieldType.Varchar, null, "创建人ID", null, false, null));
		systemFields.add(new Field("creatorName", "创建人名称", FieldType.Varchar, null, "创建人名称", null, false, null));
		systemFields.add(new Field("creatorTel", "创建人电话", FieldType.Varchar, null, "创建人电话", null, false, null));
		systemFields.add(new Field("updator", "更新人ID", FieldType.Varchar, null, "更新人ID", null, false, null));
		systemFields.add(new Field("updatorName", "更新人名称", FieldType.Varchar, null, "更新人名称", null, false, null));
		systemFields.add(new Field("updatorTel", "更新人电话", FieldType.Varchar, null, "更新人电话", null, false, null));
		systemFields.add(new Field("t01", "备用字段1", FieldType.Varchar, null, "备用字段", null, false, null));
		systemFields.add(new Field("t02", "备用字段2", FieldType.Varchar, null, "备用字段", null, false, null));
		systemFields.add(new Field("t03", "备用字段3", FieldType.Varchar, null, "备用字段", null, false, null));
		//add system field
		systemFields.add(new Field("SourceID", "源单ID", FieldType.Varchar, null, "源单ID", null, false, null));
		systemFields.add(new Field("MigrateKey", "转换key", FieldType.Varchar, null, "转换key", null, false, null));
		systemFields.add(new Field("piID", "流程实例ID", FieldType.Varchar, null, "流程实例ID", null, false, null));
		systemFields.add(new Field("mapCount", "转换次数", FieldType.Int, null, "转换次数", null, false, null));
			
		systemFields.add(new Field("rowID", "行唯一标识", FieldType.Varchar, null, "行唯一标识", null, false, null));

		for (Field field : systemFields) {
			table.getFields().remove(field);
		}

		return this.sortTableFields(systemFields, table);
	}

	/**
	 * 添加单据系统默认字段
	 * 
	 * @param table
	 * @return
	 */
	private DataTableMeta addBillSysItem(DataTableMeta table) {
		List<Field> systemFields = new ArrayList<>();
		if (table.getHead()) {
			systemFields.add(new Field("BillID", "BillID", FieldType.BillID, null, "主键ID", null, false, null));
			systemFields.add(new Field("status", "状态", FieldType.Int, null, "状态：-1 删除 ,1录入，20 ，正式，20-40业务，40审核通过",
					null, false, null));
//			systemFields.add(new Field("auditStatus", "审核状态", FieldType.Int, null, "审核状态", null, false, null));
			systemFields.add(new Field("orgId", "机构ID", FieldType.Varchar, null, "机构ID", null, false, null));
			systemFields.add(new Field("orgCode", "机构编码", FieldType.Varchar, null, "机构编码", null, false, null));
			systemFields.add(new Field("createTime", "创建时间", FieldType.TimeStamp, null, "创建时间", null, false, null));
			systemFields.add(new Field("updateTime", "更新时间", FieldType.TimeStamp, null, "更新时间", null, false, null));
			systemFields.add(new Field("creator", "创建人ID", FieldType.Varchar, null, "创建人ID", null, false, null));
			systemFields.add(new Field("creatorName", "创建人名称", FieldType.Varchar, null, "创建人名称", null, false, null));
			systemFields.add(new Field("creatorTel", "创建人电话", FieldType.Varchar, null, "创建人电话", null, false, null));
			systemFields.add(new Field("updator", "更新人ID", FieldType.Varchar, null, "更新人ID", null, false, null));
			systemFields.add(new Field("updatorName", "更新人名称", FieldType.Varchar, null, "更新人名称", null, false, null));
			systemFields.add(new Field("updatorTel", "更新人电话", FieldType.Varchar, null, "更新人电话", null, false, null));
			systemFields.add(new Field("t01", "备用字段1", FieldType.Varchar, null, "备用字段", null, false, null));
			systemFields.add(new Field("t02", "备用字段2", FieldType.Varchar, null, "备用字段", null, false, null));
			systemFields.add(new Field("t03", "备用字段3", FieldType.Varchar, null, "备用字段", null, false, null));
			//add system field
			systemFields.add(new Field("SourceID", "源单ID", FieldType.Varchar, null, "源单ID", null, false, null));
			systemFields.add(new Field("MapKey", "转换key", FieldType.Varchar, null, "转换key", null, false, null));
			systemFields.add(new Field("SrcTableName", "源表名称", FieldType.Varchar, null, "源表名称", null, false, null));
			systemFields.add(new Field("SrcKey", "源表主键key", FieldType.Varchar, null, "源表主键key", null, false, null));
			//MapKey,SourceID,SrcTableName,SrcKey
			systemFields.add(new Field("piID", "流程实例ID", FieldType.Varchar, null, "流程实例ID", null, false, null));
			systemFields.add(new Field("mapCount", "转换次数", FieldType.Int, null, "转换次数", null, false, null));
			systemFields.add(new Field("mixCondition", "混合查询", FieldType.Varchar, null, "混合查询", null, false, null));
		} else if (table.getTableType() == TableType.Table) {
			systemFields.add(new Field("BillDtlID", "单据体主键ID", FieldType.BillDtlID, null, "主键ID", null, false, null));
			systemFields.add(new Field("BillID", "外键ID", FieldType.BillID, null, "外键ID", null, false, null));
			systemFields.add(new Field("createTime", "创建时间", FieldType.TimeStamp, null, "创建时间", null, false, null));
			systemFields.add(new Field("updateTime", "更新时间", FieldType.TimeStamp, null, "更新时间", null, false, null));
			systemFields.add(new Field("orgId", "机构ID", FieldType.Varchar, null, "机构ID", null, false, null));
			systemFields.add(new Field("orgCode", "机构编码", FieldType.Varchar, null, "机构编码", null, false, null));
			//add system field
			systemFields.add(new Field("SourceID", "源单ID", FieldType.Varchar, null, "源单ID", null, false, null));
			systemFields.add(new Field("MapKey", "转换key", FieldType.Varchar, null, "转换key", null, false, null));
			systemFields.add(new Field("SrcTableName", "源表名称", FieldType.Varchar, null, "源表名称", null, false, null));
			systemFields.add(new Field("SrcKey", "源表名称", FieldType.Varchar, null, "源表名称", null, false, null));
			//MapKey,SourceID,SrcTableName,SrcKey
			//序号行内码
			systemFields.add(new Field("SN", "序号", FieldType.Int, null, "序号", null, false, null));
			systemFields.add(new Field("mixCondition", "混合查询", FieldType.Varchar, null, "混合查询", null, false, null));
			}
		systemFields.add(new Field("rowID", "行唯一标识", FieldType.Varchar, null, "行唯一标识", null, false, null));
		for (Field field : systemFields) {
			table.getFields().remove(field);
		}

		return this.sortTableFields(systemFields, table);
	}

	/**
	 * 添加字典系统默认字段
	 * 
	 * @param table
	 * @return
	 */
	private DataTableMeta addDicSysItem(DataTableMeta table) {
		List<Field> systemFields = new ArrayList<>();
		if (table.getHead()) {
			systemFields.add(new Field("ID", "ID", FieldType.BillID, null, "主键ID", null, false, null));
			systemFields.add(
					new Field("status", "字典状态", FieldType.Int, null, "字典状态：40正式，30修改中，25审核中", null, false, null));
			systemFields.add(new Field("name", "名称", FieldType.Varchar, null, "名称", null, false, null));
			systemFields.add(new Field("orgId", "机构ID", FieldType.Varchar, null, "机构ID", null, false, null));
			systemFields.add(new Field("orgCode", "机构编码", FieldType.Varchar, null, "机构编码", null, false, null));
			systemFields.add(new Field("createTime", "创建时间", FieldType.TimeStamp, null, "创建时间", null, false, null));
			systemFields.add(new Field("updateTime", "更新时间", FieldType.TimeStamp, null, "更新时间", null, false, null));
			systemFields.add(new Field("creator", "创建人ID", FieldType.Varchar, null, "创建人ID", null, false, null));
			systemFields.add(new Field("creatorName", "创建人名称", FieldType.Varchar, null, "创建人名称", null, false, null));
			systemFields.add(new Field("creatorTel", "创建人电话", FieldType.Varchar, null, "创建人电话", null, false, null));
			systemFields.add(new Field("updator", "更新人ID", FieldType.Varchar, null, "更新人ID", null, false, null));
			systemFields.add(new Field("updatorName", "更新人名称", FieldType.Varchar, null, "更新人名称", null, false, null));
			systemFields.add(new Field("updatorTel", "更新人电话", FieldType.Varchar, null, "更新人电话", null, false, null));
			systemFields.add(new Field("nodeType", "节点类型", FieldType.Int, null, "节点类型", null, false, null));
			systemFields.add(new Field("parentId", "父节点ID", FieldType.Varchar, null, "父节点ID", null, false, null));
			systemFields.add(new Field("code", "代码", FieldType.Varchar, null, "代码", null, false, null));
			systemFields.add(new Field("zhujima", "助记码", FieldType.Varchar, null, "助记码", null, false, null, true));
			systemFields.add(new Field("t01", "备用字段1", FieldType.Varchar, null, "备用字段", null, false, null));
			systemFields.add(new Field("t02", "备用字段2", FieldType.Varchar, null, "备用字段", null, false, null));
			systemFields.add(new Field("t03", "备用字段3", FieldType.Varchar, null, "备用字段", null, false, null));
			systemFields.add(new Field("SourceID", "源单ID", FieldType.Varchar, null, "源单ID", null, false, null));
			systemFields.add(new Field("MapKey", "转换key", FieldType.Varchar, null, "转换key", null, false, null));
			systemFields.add(new Field("SrcTableName", "源表名称", FieldType.Varchar, null, "源表名称", null, false, null));
			systemFields.add(new Field("SrcKey", "源表主键key", FieldType.Varchar, null, "源表主键key", null, false, null));
			systemFields.add(new Field("piID", "流程实例ID", FieldType.Varchar, null, "流程实例ID", null, false, null));
			systemFields.add(new Field("mixCondition", "混合查询", FieldType.Varchar, null, "混合查询", null, false, null));
		} else if (table.getTableType() == TableType.Table) {
			systemFields.add(new Field("BillDtlID", "字典体主键ID", FieldType.BillDtlID, null, "主键ID", null, false, null));
			systemFields.add(new Field("ID", "外键ID", FieldType.BillID, null, "外键ID", null, false, null));
			systemFields.add(new Field("orgId", "机构ID", FieldType.Varchar, null, "机构ID", null, false, null));
			systemFields.add(new Field("orgCode", "机构编码", FieldType.Varchar, null, "机构编码", null, false, null));
			systemFields.add(new Field("createTime", "创建时间", FieldType.TimeStamp, null, "创建时间", null, false, null));
			systemFields.add(new Field("updateTime", "更新时间", FieldType.TimeStamp, null, "更新时间", null, false, null));
			systemFields.add(new Field("SN", "序号", FieldType.Int, null, "序号", null, false, null));
		}
		systemFields.add(new Field("rowID", "行唯一标识", FieldType.Varchar, null, "行唯一标识", null, false, null));

		for (Field field : systemFields) {
			table.getFields().remove(field);
		}

		return this.sortTableFields(systemFields, table);
	}

	/**
	 * 修改表alert table ---删除的列 ---修改的列 ---添加的列
	 * 
	 * @param table
	 *            ds文件中table的定义
	 * @param dataSetDataSource
	 * 
	 * @param tableSchema
	 *            数据表中的schema
	 */
	public void alterTable(DataTableMeta table, String dataSetDataSource, TableSchema tableSchema) {
		List<Field> newFields = new ArrayList<>();
		List<Field> modifyFields = new ArrayList<>();
		List<String> delFields = new ArrayList<>();

		// 遍历DS定义文件
		for (Field field : table.getFields()) {

			// 新加的字段--定义文件中有，而数据表中没有
			if (!tableSchema.getColumnSchemas().containsKey(field.getFieldKey())) {
				newFields.add(field);
			} else {// 不是新增的字段，看是否修改了字段
				if (this.compareFieldModified(field, tableSchema.getColumnSchemas().get(field.getFieldKey()))) {// 是否修改了字段
					modifyFields.add(field);
				}
			}
		}

		// 遍历数据库字段
		for (String field : tableSchema.getColumnSchemas().keySet()) {
			if (this.isIDFiled(field)) {
				continue;
			}
			// 如果数据有，但ds中没有，删除之
			if (!table.containField(field)) {
				delFields.add(field);
			}
		}

		// 是否需要alert
		if (newFields.size() > 0 || modifyFields.size() > 0 || delFields.size() > 0) {
			this.alertTableSchema(table, dataSetDataSource, newFields, modifyFields, delFields);
		}
	}

	private boolean isIDFiled(String field) {
		if (StringUtil.isEmpty(field)) {
			return false;
		}

		String caseField = field.toLowerCase();
		// 系统字段过滤
		return "billid".equals(caseField.toLowerCase()) || "id".equals(caseField) || "billdtlid".equals(caseField);

	}

	/**
	 * 修改表
	 * 
	 * @param table
	 * @param dataSetDataSource
	 * @param newFields
	 * @param modifyFields
	 * @param delFields
	 */
	private void alertTableSchema(DataTableMeta table, String dataSetDataSource, List<Field> newFields,
			List<Field> modifyFields, List<String> delFields) {
		String ds = table.getDataSource();
		if (StringUtil.isEmpty(ds)) {// 局部数据源优先
			ds = dataSetDataSource;
		}
		DbPro dbPro = null;
		if (StringUtil.isEmpty(ds)) {
			dbPro = Db.use();
		} else {
			dbPro = Db.use(ds);
		}

		StringBuffer alertSql = new StringBuffer();
		alertSql.append("alter table `").append(table.getRealTableName()).append("` ");
		for (String del : delFields) {
			alertSql.append(" drop `").append(del).append("`").append(",");
		}

		for (Field field : newFields) {
			alertSql.append(" add ").append(field.getCreateSQL()).append(field.getComments()).append(",");
		}

		for (Field field : modifyFields) {
			alertSql.append(" modify ").append(field.getCreateSQL()).append(field.getComments()).append(",");
		}

		if (alertSql.lastIndexOf(",") == alertSql.length() - 1) {
			alertSql.deleteCharAt(alertSql.length() - 1);
		}

		try {
			dbPro.update(alertSql.toString());

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	/**
	 *
	 * 比较定义文件中字段与数据库中的是否修改过
	 * 
	 * @param field
	 *            ds中定义的field
	 * @param columnSchema
	 *            数据库中的列schema
	 * @return true----修改过字段 false---没有修改字段
	 * 
	 */
	private boolean compareFieldModified(Field field, ColumnSchema columnSchema) {
		// 比较类型是否兼容
		if (this.fieldTypeEquals(field.getFieldType(), columnSchema.getColumnTypeName())) {
			// 类型兼容的情况下，比较bound是否一致
			if (this.fieldBoundEquals(field.getBound(), columnSchema.getPrecision(), columnSchema.getScale())) {
				// 比较注解是否一致
				if (!this.fieldCommentEquals(field.getComment(), columnSchema.getComment())) {
					return true;
				}
			}
		} else {// 类型兼容
			return true;
		}
		return false;// 返回false，标识没有修改字段
	}

	private boolean fieldCommentEquals(String comment, String comment2) {
		if (StringUtil.isEmpty(comment)) {
			comment = "";
		}
		if (StringUtil.isEmpty(comment2)) {
			comment2 = "";
		}
		if (comment.equals(comment2)) {
			return true;
		}
		return false;
	}

	private boolean fieldBoundEquals(String bound, int precision, int scale) {
		if (StringUtil.isEmpty(bound)) {
			return true;
		}
		if (bound.contains(",")) {
			return bound.equals(precision + "," + scale);
		} else {
			return bound.equals(Integer.toString(precision));
		}
	}

	// VARCHAR,INT,BIGINT,DECIMAL,DATE,TIMESTAMP,
	private boolean fieldTypeEquals(FieldType fieldType, String columnTypeName) {
		if ("VARCHAR".equals(columnTypeName)) {
			return fieldType == FieldType.BillDtlID || fieldType == FieldType.BillID || fieldType == FieldType.ItemID
					|| fieldType == FieldType.Text || fieldType == FieldType.Varchar;
		} else if ("INT".equals(columnTypeName)) {
			return fieldType == FieldType.Int;
		} else if ("BIGINT".equals(columnTypeName)) {
			return fieldType == FieldType.Long;
		} else if ("DECIMAL".equals(columnTypeName)) {
			return fieldType == FieldType.Decimal;
		} else if ("DATE".equals(columnTypeName)) {
			return fieldType == FieldType.Date;
		} else if ("TIMESTAMP".equals(columnTypeName)) {
			return fieldType == FieldType.TimeStamp;
		}
		return false;
	}

	public BlockingQueue<DataSetMeta> getDataSetMonitorQueue() {
		return queue;
	}

	/**
	 * 系统字段靠前
	 * 
	 * @param systemFields
	 * @param table
	 * @return
	 */
	private DataTableMeta sortTableFields(List<Field> systemFields, DataTableMeta table) {
		List<Field> sourceFields = new ArrayList<>();
		sourceFields.addAll(table.getFields());
		table.getFields().clear();
		table.getFields().addAll(0, systemFields);
		table.getFields().addAll(systemFields.size(), sourceFields);
		return table;
	}

	public Map<String, BillDef> getUnmodifiesBillDefs() {
		return Collections.unmodifiableMap(this.billDefs);
	}

	public Map<String, DicDef> getUnmodifiesDicDefs() {
		return Collections.unmodifiableMap(this.dicDefs);
	}
	
	public Map<String, MigrationDef> getUnmodifiesMigrationDefs() {
		return Collections.unmodifiableMap(this.migrationDefs);
	}
}
