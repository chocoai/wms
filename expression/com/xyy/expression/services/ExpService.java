package com.xyy.expression.services;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.computer.Computer;
import com.xyy.expression.lib.OrderLib;
import com.xyy.expression.parser.Parser;

/**
 * 表达式服务处理器
 * 
 * @author evan
 * 
 */
public class ExpService {
	public static final long EXPIRE_TIME=3600000*8;
	private static ExpService service;
	private Parser parser;
	private Computer computer;
	private Map<String,PreCompileResult> parserResultCache;
    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);  

	public static ExpService instance() {
		if (service == null) {
			synchronized (ExpService.class) {
				if (service == null) {
					service = new ExpService();
					service.registerLib("o", new  OrderLib());
				}
			}
		}
		return service;

	}

	private ExpService() {
		this.parser = new Parser();
		this.computer = new Computer();
		//预编译结果缓存
		this.parserResultCache = new ConcurrentHashMap<String, PreCompileResult>();		
		this.initPreCompileCacheMonitor();	
	}
	
	private void initPreCompileCacheMonitor() {
		scheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for(String expr:parserResultCache.keySet()){
					PreCompileResult preCompileResult=parserResultCache.get(expr);
					if((System.currentTimeMillis()-preCompileResult.time)>EXPIRE_TIME){
						parserResultCache.remove(expr);
					}
					
				}
			}
		}, 3600, 3600, TimeUnit.SECONDS);
		
	}

	private Stack<Token> getParserResult(Stack<Token> stOutput) {
		Stack<Token> resultStack = new Stack<Token>();
		while (!stOutput.isEmpty()) {
			resultStack.push(stOutput.pop());
		}
		return resultStack;
	}

	public OperatorData calc(String expr, Context context) throws ExpressionCalException{
		if (!this.parserResultCache.containsKey(expr)) {
			Stack<Token> stOutput = new Stack<Token>();
			Stack<Token> stTemp = new Stack<Token>();
			try {
				parser.parse(expr, stTemp, stOutput);
				parserResultCache.put(expr, new PreCompileResult(this.getParserResult(stOutput), System.currentTimeMillis()));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExpressionCalException(e);
			}
		}
		
		Stack<OperatorData> stOutput = new Stack<OperatorData>();
		Stack<Token> tokens = this.getTokensFromResultCache(expr);
		try {
			computer.comp(tokens, context, stOutput);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExpressionCalException(e);
		}
		if (!stOutput.isEmpty()) {
			return stOutput.pop();
		}
		return null;
	}

	private Stack<Token> getTokensFromResultCache(String expr) {
		Stack<Token> result = new Stack<Token>();
		if (this.parserResultCache.containsKey(expr)) {
			Stack<Token> cache = this.parserResultCache.get(expr).getTokens();
			this.parserResultCache.get(expr).setTime(System.currentTimeMillis());//更新缓存时间
			for (int i = 0; i < cache.size(); i++) {
				result.push(cache.get(i));
			}
		}
		return result;
	}

	public Parser getParser() {
		return parser;
	}

	public Computer getComputer() {
		return computer;
	}

	public void registerLib(String ns, ILib lib) {
		this.computer.registerLib(ns, lib);
	}
	
	
	
	public static class PreCompileResult{
		private Stack<Token> tokens;
		private long time;
		
		public PreCompileResult(Stack<Token> tokens, long time) {
			super();
			this.tokens = tokens;
			this.time = time;
		}
		public Stack<Token> getTokens() {
			return tokens;
		}
		public void setTokens(Stack<Token> tokens) {
			this.tokens = tokens;
		}
		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		
		
	}

}
