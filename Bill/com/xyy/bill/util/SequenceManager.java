package com.xyy.bill.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

	

public class SequenceManager {
	private static SequenceManager _instance;
	private static final Map<String, AtomicLong> _pools = new HashMap<String, AtomicLong>();
	private static final Lock lock = new ReentrantLock();
	
	private static final String sequenceFile = "sequence.seq";
	private static String curDir = null;
	static {
		StringBuffer sb = new StringBuffer();
		File dir = new File(SequenceManager.class.getResource("/").getPath())
				.getParentFile();
		curDir = dir.getAbsolutePath() + "/";
	}

	private SequenceManager() {
		this.initSeq();
	
	}

	private List<File> getLastestNewFiles() {
		File dir = new File(curDir);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("sequence");
			}
		});
			
		if (files.length > 1) {
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					long m1 = f1.lastModified();
					long m2 = f2.lastModified();
					if (m1 > m2) {
						return 1;
					} else if (m1 == m2) {
						return 0;
					} else {
						return -1;
					}
				}
			});
		}

		return Arrays.asList(files);
	}
	
	
	/*
	 * 初始化序列方法
	 */
	private void initSeq() {
		List<File> files = this.getLastestNewFiles();
		for (File f : files) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(f);
				br = new BufferedReader(fr);
				String lineStr;
				while ((lineStr = br.readLine()) != null) {
					String[] ta = lineStr.split("=");
					if(ta.length!=2){
						continue;
					}
					String key=ta[0];
					if(_pools.containsKey(key)){
						AtomicLong oldAL=_pools.get(key);
						AtomicLong newAL= new AtomicLong(Long.parseLong(ta[1])+1);
						if(oldAL.longValue()<=newAL.longValue()){
							_pools.put(key, newAL);
						}
					}else{
						_pools.put(ta[0], new AtomicLong(Long.parseLong(ta[1])+1));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}
					if (fr != null) {
						fr.close();
					}
				} catch (Exception e2) {
					
				}

			}
		}

	}

	public static SequenceManager getInstance() {
		if (_instance == null) {
			synchronized (SequenceManager.class) {
				if (_instance == null) {
					_instance = new SequenceManager();
				}
			}
		}
		return _instance;
	}

	public long nextSequence(String sequenceName) {
		try {
			lock.lock();
			if (!this._pools.containsKey(sequenceName)) {// 生成序列
				this._pools.put(sequenceName, new AtomicLong(1));
			}
			AtomicLong atomicLong = this._pools.get(sequenceName);
			long result = atomicLong.getAndIncrement();
			this.updateSequence(sequenceName, result);
			return result;
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}

		return 0;
	}

	public long preSequence(String sequenceName) {
		try {
			lock.lock();
			if (!this._pools.containsKey(sequenceName)) {// 生成序列
				this._pools.put(sequenceName, new AtomicLong(1));
			}
			AtomicLong atomicLong = this._pools.get(sequenceName);
			long result = atomicLong.getAndAdd(0);
			this.updateSequence(sequenceName, result);
			return result;
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}

		return 0;
	}

	private void updateSequence(String sequenceName, long value) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(getSeqFile(), true);
			fw.write(sequenceName);
			fw.write("=");
			fw.write(String.valueOf(value));
			fw.write("\n");
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File getSeqFile() {
		File result = new File(curDir + sequenceFile);
		if (!result.exists()) {
			try {
				result.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (result.length() > 1024 * 1024 * 10) {// 每10M，更新一次文件
				result.renameTo(new File(curDir + "/sequence"
						+ System.currentTimeMillis() + ".seq"));
				result = new File(curDir + sequenceFile);
			}

		}
		return result;
	}

	/*public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		
		for (int i = 0; i < 5000; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for (int i = 0; i < 5; i++) {
						System.out.println(SequenceBuilder.nextSequence("cdt_test", "DB"));

					}
					
				}
			}).start();
		}
		Long end = System.currentTimeMillis();
	
		

	}*/

}
