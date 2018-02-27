package com.xyy.bill.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.plugin.activerecord.Db;

public class SequenceManager {
	private static SequenceManager _instance;
	/**
	 * 序列池,用于存放下放的序列
	 * 
	 */
	private final Map<String, AtomicInteger> _pools = new HashMap<>();
	/**
	 * 锁对象：同步锁对象
	 * 每个表一个锁对象
	 * 
	 */
	private final Map<String, Lock> locks = new HashMap<>();

	/**
	 * 私有构造函数
	 * 
	 */
	private SequenceManager() {

	}

	public static SequenceManager instance() {
		if (_instance == null) {
			synchronized (SequenceManager.class) {
				if (_instance == null) {
					_instance = new SequenceManager();
				}
			}
		}
		return _instance;
	}

	private static final String MAX_SORT_NO_SQL = "select IFNULL(max(SN),0) as maxSN from ";

	/**
	 * 获取明细最大序号
	 * 
	 * @param realTableName
	 * @return
	 */
	private int getMaxSN(String realTableName) {
		int value = Db.findFirst(MAX_SORT_NO_SQL + realTableName).getLong("maxSN").intValue();
		return value == 0 ? 1 : value;
	}

	private Lock getSequenceLock(String sequenceName) {
		if (!this.locks.containsKey(sequenceName)) {
			synchronized (SequenceManager.class) {//缩短锁程
				if (!this.locks.containsKey(sequenceName)) {
					this.locks.put(sequenceName, new ReentrantLock());
				}
			}
		}
		return this.locks.get(sequenceName);
	}

	public int nextSequence(String sequenceName) {
		Lock lock = this.getSequenceLock(sequenceName);
		try {
			lock.lock();
			if (!this._pools.containsKey(sequenceName)) {// 初始化序列
				this._pools.put(sequenceName, new AtomicInteger(getMaxSN(sequenceName)));
			}
			AtomicInteger atomicInt = this._pools.get(sequenceName);
			int result = atomicInt.getAndIncrement();
			return result;
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}

		return 0;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 500; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 5; i++) {
						System.out.println(SequenceManager.instance().nextSequence("abc"));
					}

				}
			}).start();
		}
	}

}
