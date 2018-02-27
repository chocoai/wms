/**
 * Copyright (c) 2011-2017, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xyy.bill.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;

/**
 * Scanner.
 */
public abstract class BillScanner {
	private Timer timer;
	private TimerTask task;
	private File rootDir;
	private int interval;
	private boolean running = false;
	private final Map<String, TimeSize> preScan = new HashMap<String, TimeSize>();
	private final Map<String, TimeSize> curScan = new HashMap<String, TimeSize>();
	// 数据集配置跟踪
	private final Map<String, TimeSize> preDSScan = new HashMap<String, TimeSize>();
	private final Map<String, TimeSize> curDSScan = new HashMap<String, TimeSize>();

	public BillScanner(String rootDir, int interval) {
		if (StrKit.isBlank(rootDir))
			throw new IllegalArgumentException("The parameter rootDir can not be blank.");
		this.rootDir = new File(rootDir);
		if (!this.rootDir.isDirectory())
			throw new IllegalArgumentException("The directory " + rootDir + " is not exists.");
		if (interval <= 0)
			throw new IllegalArgumentException("The parameter interval must more than zero.");
		this.interval = interval;
	}

	public abstract void onChange(Map<String, TimeSize> pre, Map<String, TimeSize> cur);

	public abstract void onDSChange(Map<String, TimeSize> preDS, Map<String, TimeSize> curDS);

	public abstract void onInitScanner();

	private void working() {
		//扫描根目录
		scan(rootDir);
		/*
		 * 比较preScan和curScan，是否有变化，如果有变化，触发对应的变化处理
		 */
		compare();
		//交换当前扫描与前一次扫面池
		preScan.clear();
		preDSScan.clear();
		preScan.putAll(curScan);
		preDSScan.putAll(curDSScan);
		curScan.clear();
		curDSScan.clear();
	}

	/**
	 * 递归扫描
	 * 
	 * @param file
	 */
	private void scan(File file) {
		if (file == null || !file.exists())
			return;

		if (file.isFile()) {
			String filename = file.getName();
			//以"_"或"$"开头的文件，不做监测;config.xml文件不做监测
			if (filename.startsWith("_") || filename.startsWith("$") || filename.equals("config.xml")) {
				return;
			} else {
				try {
					//扫面结果放到curScan中
					curScan.put(file.getCanonicalPath(), new TimeSize(file.lastModified(), file.length()));
					//对字典和数据集的DS文件进行跟踪
					if ((filename.startsWith("Bill_") || filename.startsWith("Dic_") || filename.startsWith("Migration_")) && filename.indexOf("_DS") > 0) {
						curDSScan.put(file.getCanonicalPath(), new TimeSize(file.lastModified(), file.length()));
					}

				} catch (IOException e) {
					LogKit.error(e.getMessage(), e);
				}
			}
		} else if (file.isDirectory()) {
			File[] fs = file.listFiles();
			if (fs != null)
				for (File f : fs)
					scan(f);//递归扫面
		}
	}

	/**
	 * 比较文件系统是否有修改
	 */
	private void compare() {
		// 第一次启动，触发onInitScanner事件
		if (preScan.size() == 0) {
			onInitScanner();
			return;
		}
		// 比较curScan与preScan是否一致，触发onChange事件
		if (!preScan.equals(curScan)) {
			Map<String, TimeSize> pre = new HashMap<String, TimeSize>();
			pre.putAll(preScan);
			Map<String, TimeSize> cur = new HashMap<String, TimeSize>();
			cur.putAll(curScan);
			onChange(pre,cur);//配置文件变化
		}
		// 比较单据数据集和字典数据集的变化，触发onDSChange事件，可能会导致数据schema发生改变
		if (!preDSScan.equals(curDSScan)) {
			Map<String, TimeSize> preDS = new HashMap<String, TimeSize>();
			preDS.putAll(preDSScan);
			Map<String, TimeSize> curDS = new HashMap<String, TimeSize>();
			curDS.putAll(curDSScan);
			onDSChange(preDS, curDSScan);
		}
	}

	public void start() {
		if (!running) {
			timer = new Timer("XYY-Bill-Scanner", true);
			task = new TimerTask() {
				public void run() {
					working();
				}
			};
			timer.schedule(task, 1010L * interval, 1010L * interval);
			running = true;
		}
	}

	public void stop() {

		if (running) {
			timer.cancel();
			task.cancel();
			running = false;
		}

	}

}

class TimeSize {

	final long time;
	final long size;

	public TimeSize(long time, long size) {
		this.time = time;
		this.size = size;
	}

	public int hashCode() {
		return (int) (time ^ size);
	}

	public boolean equals(Object o) {
		if (o instanceof TimeSize) {
			TimeSize ts = (TimeSize) o;
			return ts.time == this.time && ts.size == this.size;
		}
		return false;
	}

	public String toString() {
		return "[t=" + time + ", s=" + size + "]";
	}
}
