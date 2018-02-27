package com.xyy.bill.controller;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.xyy.bill.engine.BillPlugin;

public class Test {
	private static  BillPlugin billPlugin;

	public static void main(String[] args) {
		//
		ScriptEngineManager sem=new ScriptEngineManager();
		ScriptEngine engine=sem.getEngineByName("nashorn");
		
		
		
		
		initPlugins();
		

	}

	private static void initPlugins() {
		billPlugin=new BillPlugin();
		billPlugin.start();
		
	}

}

