package com.xyy.erp.platform.system.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 缓存的结构： (1) 缓存加载时机：全量加载，惰性加载 (2) 需要加入GC机制 (3) 在缓存里面要放那些信息
 * 
 * 
 * sku---》1.可用库存； 2.update aaaa set wmd=wmd+/-?
 * 
 * @author evan
 *
 */
public class WMSCache {
	public static WMSCache MAIN = new WMSCache();
	private final ScheduledExecutorService _gcThread = Executors.newScheduledThreadPool(1);
	private final ExecutorService _cacheThead = Executors.newFixedThreadPool(1); // 缓存可用库存
	private final Map<String, CacheItem> _cache = new ConcurrentHashMap<>();
	private final Map<String, Lock> lock_flag = new ConcurrentHashMap<>();
	private  final BlockingQueue<CacheItem> updateCacheItem = new LinkedBlockingQueue<>();

//	private static final long GC_TIME = 24 * 3600 * 1000;
//	private static final long CACHE_TIME = 60 * 1000;

	/**
	 * 判断该商品是否在缓存
	 * 
	 * @param squ
	 * @return
	 */
	public boolean containSku(String sku) {
		return _cache.containsKey(sku);
	}

	/**
	 * 获取sku对应的锁对象
	 * 
	 * @param sku
	 * @return
	 */
	public Lock getSkuLock(String sku) {
		if (!lock_flag.containsKey(sku)) {
			synchronized (WMSCache.class) {
				if (!lock_flag.containsKey(sku)) {//
					// lock_flag.put(sku, new ReentrantLock());
					lock_flag.put(sku, new ReentrantLock());
				}
			}
		}
		return lock_flag.get(sku);
	}

	/**
	 * 加载sku缓存
	 * 
	 * @param sku
	 * @return
	 */
	public CacheItem loadCacheItem(String sku) {
		if (!_cache.containsKey(sku)) {
			Lock lock = this.getSkuLock(sku);
			try {
				lock.lock();
				if (!_cache.containsKey(sku)) {
					this._loadCacheItem(sku);
				}
			} finally {
				lock.unlock();
			}

		}
		return _cache.get(sku);
	}

	/**
	 * 获取可用的库存
	 */
	public BigDecimal loadAvailableKucun(String sku) {
		if (!_cache.containsKey(sku)) {
			Lock lock = this.getSkuLock(sku);
			try {
				lock.lock();
				if (!_cache.containsKey(sku)) {
					this._loadCacheItem(sku);
				}
			} finally {
				lock.unlock();
			}
		}
		return _cache.get(sku).getKucun();
	}

	/**
	 * 生成预占用
	 * 
	 * @param sku
	 * @param count
	 * @return
	 */
	public boolean preemption(String sku, BigDecimal count) {
		Lock lock = this.getSkuLock(sku);
		try {
			lock.lock();
			CacheItem item = loadCacheItem(sku);
			if (item.getKucun().compareTo(count) >= 0) {
				item.setKucun(item.getKucun().subtract(count));// 产生预占
				updateCacheItem.put(item);
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LogKit.error(ex.getMessage(), ex);
		}

		finally {
			lock.unlock();
		}
		return false;
	}
	
	
	/**
	 * 减库存
	 * 
	 * @param sku
	 * @param count
	 * @return
	 */
	public void subtractSkuAvailable(String sku, BigDecimal count) {
		Lock lock = this.getSkuLock(sku);
		try {
			lock.lock();
			CacheItem item = loadCacheItem(sku);
			item.setKucun(item.getKucun().subtract(count));// 减库存
			updateCacheItem.put(item);
		} catch (Exception ex) {
			ex.printStackTrace();
			LogKit.error(ex.getMessage(), ex);
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * 添加可用库存
	 * 
	 * @param sku
	 * @param count
	 * @return
	 */
	public void addSkuAvailable(String sku, BigDecimal count) {
		if(count.compareTo(BigDecimal.ZERO)<0){
			LogKit.error(sku+"====添加库存数量"+count+"==小于零===");
			return;
		}
		Lock lock = this.getSkuLock(sku);
		try {
			lock.lock();
			CacheItem item = loadCacheItem(sku);
			item.setKucun(item.getKucun().add(count));// 产生预占
			updateCacheItem.put(item);
		} catch (Exception ex) {
			ex.printStackTrace();
			LogKit.error(ex.getMessage(), ex);
		} finally {
			lock.unlock();
		}
	}

	public Map<String, CacheItem> get_cache() {
		return _cache;
	}

	/**
	 * 加载sku的可用库存
	 * 
	 * @param sku;
	 */
	private void _loadCacheItem(String sku) {
		Record shangping_kucun_r = Db.findFirst(
				"select kexiaoshuliang,b.shangpinbianhao,a.shangpinId from xyy_erp_bill_shangpinzongkucun a, xyy_erp_dic_shangpinjibenxinxi b where a.shangpinId = b.goodsid and a.shangpinId=?",
				sku);
		if (shangping_kucun_r == null) {// 总库存没有该商品，库存数量为0
			_cache.put(sku, new CacheItem(sku, "lihang", BigDecimal.ZERO));
		} else {
			// 查占用表
			Record zhanyong_r = Db.findFirst(
					"select sum(zhanyongshuliang) as zhangyong from xyy_erp_bill_shangpinkucunzhanyong where shangpinId=? ",
					sku);
			BigDecimal kucun = shangping_kucun_r.getBigDecimal("kexiaoshuliang");
			BigDecimal zhangyong = BigDecimal.ZERO;
			if (zhanyong_r != null && zhanyong_r.getBigDecimal("zhangyong") != null) {
				zhangyong = zhanyong_r.getBigDecimal("zhangyong");
			}
			
			if(kucun.compareTo(zhangyong)>=0){
				CacheItem item = new CacheItem(sku, shangping_kucun_r.getStr("shangpinbianhao"), kucun.subtract(zhangyong));
				_cache.put(sku, item);
			}else{
				CacheItem item = new CacheItem(sku, shangping_kucun_r.getStr("shangpinbianhao"), BigDecimal.ZERO);
				_cache.put(sku, item);
			}
			
			
			
		}
	}

	private WMSCache() {
		this.init();
		this.restoreCache();
	}

	private void restoreCache() {
		try {
			List<Record> cacheKucun = Db
					.find("select goodsid, shangpinbianhao, kucunshuliang from xyy_erp_cachemap_kucun");
			for (Record r : cacheKucun) {
				CacheItem item = new CacheItem(r.getStr("goodsid"), r.getStr("shangpinbianhao"),
						r.getBigDecimal("kucunshuliang"));
				_cache.put(r.getStr("goodsid"), item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LogKit.error(ex.getMessage(), ex);   
		}
	}

	private void init() {
		// this._gcThread.scheduleWithFixedDelay(new Runnable() {
		// @Override
		// public void run() {
		// for (String sku : _cache.keySet()) {
		// long curTime = System.currentTimeMillis();
		// long endTime = curTime - _cache.get(sku).getUseTime();
		// if (endTime > GC_TIME) {
		// _cache.remove(sku);
		// }
		// }
		// }
		// }, 30, 60, TimeUnit.MINUTES);

		this._cacheThead.submit(new Runnable(){
			@Override
			public void run() {
				while(true){
					try {
						CacheItem item=updateCacheItem.take();
						saveOrUpdateItem(item);												
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}

			//select goodsid, shangpinbianhao, kucunshuliang from xyy_erp_cachemap_kucun
			private void saveOrUpdateItem(CacheItem item) {//保存或跟新
				int result = Db.update("update xyy_erp_cachemap_kucun set kucunshuliang = ? where shangpinbianhao = ?", item.getKucun(), item.getCode());
				if(result <= 0){
					Record r = new Record();
					r.set("goodsid", item.getSku());
					r.set("shangpinbianhao", item.getCode());
					r.set("kucunshuliang", item.getKucun());
					Db.save("xyy_erp_cachemap_kucun", r);
				}
				
			}
			
		});
	}

	public static class CacheItem {
		private String sku;// 商品编号
		private String name;// 商品名称
		private String code;
		private BigDecimal kucun;// 可用库存
		private long loadTime;
		private long useTime;

		public CacheItem(String sku, String code, BigDecimal kucun) {
			super();
			this.sku = sku;
			this.code = code;
			this.kucun = kucun;
		}

		public CacheItem(String sku, BigDecimal kucun) {
			this();
			this.sku = sku;
			this.kucun = kucun;
		}

		public CacheItem() {
			super();
			this.loadTime = System.currentTimeMillis();
			this.useTime = System.currentTimeMillis();
		}

		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BigDecimal getKucun() {
			return kucun;
		}

		public void setKucun(BigDecimal kucun) {
			this.kucun = kucun;
		}

		public long getLoadTime() {
			return loadTime;
		}

		public void setLoadTime(long loadTime) {
			this.loadTime = loadTime;
		}

		public long getUseTime() {
			return useTime;
		}

		public void setUseTime(long useTime) {
			this.useTime = useTime;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}

}
