package com.xyy.bill.engine;

import com.xyy.bill.render.html.JSFunction;
import com.xyy.bill.render.html.JSFunction.JSWrite;
import com.xyy.bill.render.html.JSWriter;

public class TestFun {
	public static void main(String[] args) {

		JSFunction global = new JSFunction("global");
		JSFunction main = new JSFunction("main", "$scope");
		main.append("scope.hello=\"hello,world!\";");
		main.append("$scope.user={};");
		main.append("$scope.v={value:\"789\"};");
		JSFunction callme = new JSFunction();
		callme.append("alert('123');");
		callme.append("if(a>b)").append("{").append("$scope.save()").append("}");
		
		main.append("$scope.callme=" + callme.toString()).write(";");
		
		main.append("$scope.watch("+"\"a+b\","+new JSFunction("_testWatch").jswrite(new JSWrite(){
			@Override
			public void write(JSWriter write) {
				write.append("a=b").append(";");
				
			}
		})).append(");");
		// System.out.println(main.toString(true));

		global.append("var main=angular.module('xyyerp', []);");
		global.append("main.controller('xyyerp.billCtrl'," + main.toString() + ")");
		System.out.println(global.toBareString());

	}
}
